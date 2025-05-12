package com.fastcampus.paymentapi.sdk.service;

import com.fastcampus.common.exception.BaseException;
import com.fastcampus.common.exception.SdkErrorCode;
import com.fastcampus.paymentapi.sdk.dto.SdkCheckResponse;
import com.fastcampus.paymentapi.sdk.dto.SdkIssueResponse;
import com.fastcampus.paymentapi.sdk.entity.SdkKey;
import com.fastcampus.paymentapi.sdk.repository.SdkKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SdkKeyService {

    private final SdkKeyRepository sdkKeyRepository;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${sdk.ttl-days}")
    private long sdkTtlDays;

    public SdkIssueResponse issueSdkKey(Long merchantId) {
        if (merchantId == null) {
            throw new BaseException(SdkErrorCode.INVALID_MERCHANT);
        }

        try {
            String sdkKey = "sdk_" + UUID.randomUUID();
            String secretKey = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
            LocalDateTime expiresAt = LocalDateTime.now().plusDays(sdkTtlDays);

            SdkKey saved = sdkKeyRepository.save(new SdkKey(merchantId, sdkKey, secretKey, expiresAt));

            String redisKey = "sdk:key:" + sdkKey;
            redisTemplate.opsForValue().set(
                    redisKey,
                    merchantId.toString(),
                    Duration.ofDays(sdkTtlDays)
            );

            return new SdkIssueResponse(saved.getSdkKey(), saved.getExpiresAt());

        } catch (DataIntegrityViolationException e) {
            throw new BaseException(SdkErrorCode.MERCHANT_SDK_ALREADY_EXISTS);
        } catch (Exception e) {
            throw new BaseException(SdkErrorCode.SDK_ISSUE_FAILED);
        }
    }

    public SdkCheckResponse checkSdkKey(String sdkKey) {
        String redisKey = "sdk:key:" + sdkKey;

        String cached = redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            return new SdkCheckResponse(true, Long.parseLong(cached));
        }

        Optional<SdkKey> sdkKeyOptional = sdkKeyRepository.findBySdkKey(sdkKey);
        if (sdkKeyOptional.isPresent()) {
            SdkKey sdkKeyEntity = sdkKeyOptional.get();

            if (sdkKeyEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new BaseException(SdkErrorCode.EXPIRED_SDK_KEY);
            }

            Long foundMerchantId = sdkKeyEntity.getMerchantId();
            redisTemplate.opsForValue().set(
                    redisKey,
                    String.valueOf(foundMerchantId),
                    Duration.ofDays(sdkTtlDays)
            );
            return new SdkCheckResponse(true, foundMerchantId);
        }

        throw new BaseException(SdkErrorCode.INVALID_SDK_KEY);
    }

    public boolean verifyKeyOwnership(String sdkKey, Long merchantId) {
        String redisKey = "sdk:key:" + sdkKey;
        String cachedValue = redisTemplate.opsForValue().get(redisKey);

        // redis 우선조회, 이후 DB
        if (cachedValue != null) {
            return cachedValue.equals(merchantId.toString());
        }

        Optional<SdkKey> sdkKeyOptional = sdkKeyRepository.findBySdkKey(sdkKey);
        if (sdkKeyOptional.isPresent()) {
            SdkKey sdkKeyEntity = sdkKeyOptional.get();

            if (sdkKeyEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                return false;
            }
            boolean isOwner = sdkKeyEntity.getMerchantId().equals(merchantId);
            if (isOwner) {
                redisTemplate.opsForValue().set(
                        redisKey,
                        String.valueOf(merchantId),
                        Duration.ofDays(sdkTtlDays)
                );
            }
            return isOwner;
        }
        return false;
    }
}

