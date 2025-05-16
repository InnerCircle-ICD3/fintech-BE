package com.fastcampus.paymentapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class SdkKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sdkKey;      // 공개키 (헤더에 들어감)
    private String secretKey;   // HMAC용 비밀 키 (서명 검증용)

    private Long merchantId;

    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;

    public SdkKey(Long merchantId, String sdkKey, String secretKey, LocalDateTime expiresAt) {
        this.merchantId = merchantId;
        this.sdkKey = sdkKey;
        this.secretKey = secretKey;
        this.issuedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }
}
