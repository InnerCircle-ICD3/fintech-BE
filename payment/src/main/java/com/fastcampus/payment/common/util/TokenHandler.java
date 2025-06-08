package com.fastcampus.payment.common.util;

import com.fastcampus.payment.entity.Payment;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class TokenHandler {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    CommonUtil commonUtil;

    private Key signingKey;
    private static String ID_KEY = "paymentId";

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 거래 ID 기반 JWT QR 토큰 생성
     */
    public String generateTokenWithPayment(Payment payment) {
        long now = System.currentTimeMillis();
        LocalDateTime expTime = payment.getLastTransaction().getExpireAt();
        Date expDate = commonUtil.convertToDate(expTime);
        return Jwts.builder()
                .setSubject("qr_token")
                .claim(ID_KEY, payment.getId())
                .setIssuedAt(new Date(now))
                .setExpiration(expDate)
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

        return claims.get(ID_KEY, Long.class);
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
