package com.fastcampus.paymentapi.sdk.service;

import com.fastcampus.common.exception.HttpException;
import com.fastcampus.common.exception.SdkErrorCode;
import com.fastcampus.common.util.AppClock;
import com.fastcampus.paymentapi.sdk.dto.SdkCheckResponse;
import com.fastcampus.paymentapi.sdk.dto.SdkIssueResponse;
import com.fastcampus.paymentapi.sdk.entity.SdkKey;
import com.fastcampus.paymentapi.sdk.repository.SdkKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SdkKeyService {

    private final SdkKeyRepository sdkKeyRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final AppClock appClock;

    @Value("${sdk.ttl-days}")
    private long sdkTtlDays;

    public SdkIssueResponse issueSdkKey(Long merchantId) {
        if (merchantId == null) {
            throw new HttpException(SdkErrorCode.INVALID_MERCHANT);
        }

        try {
            String sdkKey = "sdk_" + UUID.randomUUID();
            String secretKey = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
            LocalDateTime issuedAt = LocalDateTime.now(appClock.getClock());
            LocalDateTime expiresAt = issuedAt.plusDays(sdkTtlDays);

            SdkKey saved = sdkKeyRepository.save(new SdkKey(merchantId, sdkKey, secretKey, issuedAt, expiresAt));

            String redisKey = "sdk:key:" + sdkKey;
            redisTemplate.opsForValue().set(
                    redisKey,
                    merchantId.toString(),
                    Duration.ofDays(sdkTtlDays)
            );

            return new SdkIssueResponse(saved.getSdkKey(), saved.getExpiresAt());

        } catch (DataIntegrityViolationException e) {
            throw new HttpException(SdkErrorCode.MERCHANT_SDK_ALREADY_EXISTS);
        } catch (Exception e) {
            throw new HttpException(SdkErrorCode.SDK_ISSUE_FAILED);
        }
    }

    public SdkCheckResponse checkSdkKey(String sdkKey) {
        String redisKey = "sdk:key:" + sdkKey;

        String cached = redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            return new SdkCheckResponse(true, Long.parseLong(cached));
        }

        SdkKey sdkKeyEntity = loadValidSdkKey(sdkKey);
        Long merchantId = sdkKeyEntity.getMerchantId();

        redisTemplate.opsForValue().set(
                redisKey,
                merchantId.toString(),
                Duration.ofDays(sdkTtlDays)
        );

        return new SdkCheckResponse(true, merchantId);
    }

    public boolean verifyKeyOwnership(String sdkKey, Long merchantId) {
        String redisKey = "sdk:key:" + sdkKey;
        String cachedValue = redisTemplate.opsForValue().get(redisKey);

        if (cachedValue != null) {
            return cachedValue.equals(merchantId.toString());
        }

        try {
            SdkKey sdkKeyEntity = loadValidSdkKey(sdkKey);
            boolean isOwner = sdkKeyEntity.getMerchantId().equals(merchantId);

            if (isOwner) {
                redisTemplate.opsForValue().set(
                        redisKey,
                        merchantId.toString(),
                        Duration.ofDays(sdkTtlDays)
                );
            }

            return isOwner;
        } catch (HttpException e) {
            return false;
        }
    }

    public void validateOwnershipOrThrow(String sdkKey, Long merchantId) {
        if (!verifyKeyOwnership(sdkKey, merchantId)) {
            throw new HttpException(SdkErrorCode.INVALID_SDK_KEY);
        }
    }

    private SdkKey loadValidSdkKey(String sdkKey) {
        Optional<SdkKey> optional = sdkKeyRepository.findBySdkKey(sdkKey);
        if (optional.isEmpty()) {
            throw new HttpException(SdkErrorCode.INVALID_SDK_KEY);
        }

        SdkKey sdkKeyEntity = optional.get();
        if (sdkKeyEntity.getExpiresAt().isBefore(LocalDateTime.now(appClock.getClock()))) {
            throw new HttpException(SdkErrorCode.EXPIRED_SDK_KEY);
        }

        return sdkKeyEntity;
    }
}