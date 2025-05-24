package com.fastcampus.backoffice.service;

import com.fastcampus.backoffice.dto.ApiKeyDto;
import com.fastcampus.backoffice.entity.ApiKey;
import com.fastcampus.backoffice.entity.Merchant;
import com.fastcampus.backoffice.repository.ApiKeyRepository;
import com.fastcampus.backoffice.repository.MerchantRepository;
import com.fastcampus.common.exception.code.MerchantErrorCode;
import com.fastcampus.common.exception.exception.DuplicateKeyException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Transactional
    public ApiKeyDto generateApiKey(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(() -> new RuntimeException("Merchant not found"));

        if (apiKeyRepository.existsByMerchant_MerchantIdAndActiveTrue(merchantId)) {
            throw DuplicateKeyException.of(MerchantErrorCode.DUPLICATE_API_KEY);
        }

        // JWT 토큰 생성
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
        String token = Jwts.builder()
            .setSubject(merchantId.toString())
            .claim("merchantName", merchant.getName())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(key)
            .compact();

        // API 키 저장
        ApiKey apiKey = new ApiKey();
        apiKey.setMerchant(merchant);
        apiKey.setEncryptedKey(token);
        apiKey.setActive(true);
        apiKey.setExpiredAt(LocalDateTime.now().plusYears(1));

        ApiKey savedApiKey = apiKeyRepository.save(apiKey);
        return convertToDto(savedApiKey);
    }

    @Transactional
    public ApiKeyDto reissueApiKey(Long merchantId, String oldKey) {
        // 기존 API 키 찾기
        ApiKey oldApiKey = apiKeyRepository.findByEncryptedKey(oldKey)
            .orElseThrow(() -> new RuntimeException("API Key not found"));
        System.out.println("oldApiKey = " + oldApiKey);
        System.out.println("merchantId = " + oldApiKey.getMerchant().getMerchantId());
        // 가맹점 ID 검증
        if (!oldApiKey.getMerchant().getMerchantId().equals(merchantId)) {
            throw new RuntimeException("Invalid merchant ID for this API key");
        }

        // 기존 API 키 비활성화
        System.out.println("ActivityBefore = " + oldApiKey.getActive());
        apiKeyRepository.deactivateActiveKeyByMerchantId(merchantId);
        System.out.println("ActivityAfter = " + oldApiKey.getActive());


        // 새로운 API 키 발급
        return generateApiKey(merchantId);
    }

    @Transactional(readOnly = true)
    public List<ApiKeyDto> getApiKeys(Long merchantId) {
        return apiKeyRepository.findByMerchant_MerchantId(merchantId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deactivateApiKey(String key) {
        ApiKey apiKey = apiKeyRepository.findByEncryptedKey(key)
            .orElseThrow(() -> new RuntimeException("API Key not found"));
        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
    }

    private ApiKeyDto convertToDto(ApiKey apiKey) {
        ApiKeyDto dto = new ApiKeyDto();
        dto.setId(apiKey.getKeysId());
        dto.setKey(apiKey.getEncryptedKey());
        dto.setActive(apiKey.getActive());
        dto.setExpiredAt(apiKey.getExpiredAt());
        return dto;
    }
} 