package com.fastcampus.paymentapi.initqr.dto;

import com.fastcampus.paymentapi.initqr.entity.PaymentStatus;
import com.fastcampus.paymentapi.initqr.entity.QrStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QrVerificationResponse {

    private final QrStatus qrStatus;
    private final PaymentStatus paymentStatus;
    private final LocalDateTime expiresAt;
    private final String message;

    private QrVerificationResponse(QrStatus qrStatus, PaymentStatus paymentStatus, LocalDateTime expiresAt, String message) {
        this.qrStatus = qrStatus;
        this.paymentStatus = paymentStatus;
        this.expiresAt = expiresAt;
        this.message = message;
    }

    public static QrVerificationResponse expired(PaymentStatus status, LocalDateTime expiresAt, String msg) {
        return new QrVerificationResponse(QrStatus.EXPIRED, status, expiresAt, msg);
    }

    public static QrVerificationResponse used(PaymentStatus status, LocalDateTime expiresAt, String msg) {
        return new QrVerificationResponse(QrStatus.USED, status, expiresAt, msg);
    }

    public static QrVerificationResponse valid(PaymentStatus status, LocalDateTime expiresAt, String msg) {
        return new QrVerificationResponse(QrStatus.VALID, status, expiresAt, msg);
    }

    public static QrVerificationResponse invalid(String msg) {
        return new QrVerificationResponse(QrStatus.INVALID, null, null, msg);
    }
}
