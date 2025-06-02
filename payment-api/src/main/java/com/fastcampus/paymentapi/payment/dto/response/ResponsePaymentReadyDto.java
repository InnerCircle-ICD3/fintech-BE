package com.fastcampus.paymentapi.payment.dto.response;

import java.time.LocalDateTime;

public class ResponsePaymentReadyDto {
    private final String token;
    private final LocalDateTime expiresAt;

    public ResponsePaymentReadyDto(String token, LocalDateTime expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}