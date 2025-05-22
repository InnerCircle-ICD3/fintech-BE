package com.fastcampus.paymentcore.core.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
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
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("transactionId", Long.class);
    }

    /**
     * 결제 준비용 JWT 토큰 생성 (별도 Claim 없이 사용)
     */
    public String generateTokenPaymentReady() {
        long now = System.currentTimeMillis();
        long exp = now + Long.parseLong(ttlQr) * 1000;

        return Jwts.builder()
                .setSubject("payment_ready_token")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
