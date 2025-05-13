package com.fastcampus.paymentapi.payment.dto;

import com.fastcampus.paymentapi.payment.entity.PaymentStatus;
import com.fastcampus.paymentapi.payment.entity.QrStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QrVerificationResponse {
    private QrStatus qrStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime expiresAt;
    private String message;
}
