package com.fastcampus.payment.servicedto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class PaymentReadyResponse {
    private final String transactionToken;
    private final LocalDateTime expiresAt;
}