package com.fastcampus.paymentapi.service;

import com.fastcampus.paymentapi.dto.SdkIssueResponse;
import com.fastcampus.paymentapi.entity.SdkKey;
import com.fastcampus.paymentapi.repository.SdkKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SdkKeyService {

    private final SdkKeyRepository sdkKeyRepository;

    public SdkIssueResponse issueSdkKey(Long merchantId) {
        String sdkKey = "sdk_" + UUID.randomUUID();
        String secretKey = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());

        LocalDateTime expiresAt = LocalDateTime.now().plusMonths(6);

        SdkKey saved = sdkKeyRepository.save(new SdkKey(merchantId, sdkKey, secretKey, expiresAt));
        return new SdkIssueResponse(saved.getSdkKey(), saved.getExpiresAt());
    }
}
