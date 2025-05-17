package com.fastcampus.paymentapi.initqr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QrInitResponse {
    private final String transactionToken;
    private final LocalDateTime expiresAt;
}