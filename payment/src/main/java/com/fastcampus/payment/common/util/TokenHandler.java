package com.fastcampus.payment.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenHandler {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${lifetime.qr}")
    private String ttlQr; // 단위: 초

    private Key signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 거래 ID 기반 JWT QR 토큰 생성
     */
    public String generateTokenWithTransactionId(Long transactionId) {
        long now = System.currentTimeMillis();
        long exp = now + Long.parseLong(ttlQr) * 1000;

        return Jwts.builder()
                .setSubject("qr_token")
                .claim("transactionId", transactionId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT QR 토큰에서 거래 ID 추출
     */
    public Long decodeQrToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("transactionId", Long.class);
    }

    /**
     * JWT QR 토큰에서 전체 Claims 반환
     */
    public Claims decodeQrTokenToClaims(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
