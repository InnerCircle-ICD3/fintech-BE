package com.fastcampus.backoffice.service;

import com.fastcampus.backoffice.dto.ApiKeyDto;
import com.fastcampus.backoffice.entity.ApiKey;
import com.fastcampus.backoffice.entity.Merchant;
import com.fastcampus.backoffice.repository.ApiKeyRepository;
import com.fastcampus.backoffice.repository.MerchantRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;

    @Value("${jwt.secret}")
    private String base64Secret;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(base64Secret.getBytes());
    }

    private static final long API_KEY_EXPIRATION_TIME = 365L * 24 * 60 * 60 * 1000; // 1 year in milliseconds

    private String generateToken(String merchantId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + API_KEY_EXPIRATION_TIME);

        Map<String, Object> claims = new HashMap<>();
        claims.put("merchantId", merchantId);
        claims.put("type", "API_KEY");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(merchantId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Transactional
    public ApiKeyDto generateApiKey(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(() -> new RuntimeException("Merchant not found"));

        String token = generateToken(merchantId.toString());

        ApiKey apiKey = new ApiKey();
        apiKey.setMerchant(merchant);
        apiKey.setKey(token);
        apiKey.setActive(true);
        apiKey.setExpiredAt(LocalDateTime.now().plusYears(1));

        ApiKey savedApiKey = apiKeyRepository.save(apiKey);
        return convertToDto(savedApiKey);
    }

    @Transactional
    public ApiKeyDto reissueApiKey(Long merchantId, String oldKey) {
        // 기존 API 키 찾기
        ApiKey oldApiKey = apiKeyRepository.findByKey(oldKey)
            .orElseThrow(() -> new RuntimeException("API Key not found"));

        // 가맹점 ID 검증
        if (!oldApiKey.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("Invalid merchant ID for this API key");
        }

        // 기존 API 키 비활성화
        oldApiKey.setActive(false);
        apiKeyRepository.save(oldApiKey);

        // 새로운 API 키 발급
        return generateApiKey(merchantId);
    }

    @Transactional
    public ApiKeyDto renewApiKey(Long merchantId, String currentKey) {
        // 현재 API 키 찾기
        ApiKey currentApiKey = apiKeyRepository.findByKey(currentKey)
            .orElseThrow(() -> new RuntimeException("API Key not found"));

        // 가맹점 ID 검증
        if (!currentApiKey.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("Invalid merchant ID for this API key");
        }

        // 현재 API 키가 활성 상태인지 확인
        if (!currentApiKey.isActive()) {
            throw new RuntimeException("API Key is not active");
        }

        // 새로운 JWT 토큰 생성
        String newToken = generateToken(merchantId.toString());
        
        // API 키 업데이트
        currentApiKey.setKey(newToken);
        currentApiKey.setExpiredAt(LocalDateTime.now().plusYears(1));
        ApiKey renewedApiKey = apiKeyRepository.save(currentApiKey);
        
        return convertToDto(renewedApiKey);
    }

    @Transactional(readOnly = true)
    public List<ApiKeyDto> getApiKeys(Long merchantId) {
        return apiKeyRepository.findByMerchantId(merchantId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deactivateApiKey(String key) {
        ApiKey apiKey = apiKeyRepository.findByKey(key)
            .orElseThrow(() -> new RuntimeException("API Key not found"));
        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
    }

    private ApiKeyDto convertToDto(ApiKey apiKey) {
        ApiKeyDto dto = new ApiKeyDto();
        dto.setId(apiKey.getId());
        dto.setKey(apiKey.getKey());
        dto.setActive(apiKey.isActive());
        dto.setCreatedAt(apiKey.getCreatedAt());
        dto.setExpiredAt(apiKey.getExpiredAt());
        return dto;
    }
} 