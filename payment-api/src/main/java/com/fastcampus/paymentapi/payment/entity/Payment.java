package com.fastcampus.paymentapi.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sdkKey;
    private LocalDateTime expiresAt;
    private Long merchantId;
    private Long merchantOrderId;
    private Long amount;

    @Column(unique = true, nullable = false)
    private String qrToken;

    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public Payment(String sdkKey, Long merchantId, Long merchantOrderId, Long amount,
                   String qrToken, LocalDateTime expiresAt, LocalDateTime requestedAt) {
        this.sdkKey = sdkKey;
        this.merchantId = merchantId;
        this.merchantOrderId = merchantOrderId;
        this.amount = amount;
        this.qrToken = qrToken;
        this.expiresAt = expiresAt;
        this.requestedAt = requestedAt;
        this.status = PaymentStatus.READY;
    }

    public void complete() {
        this.status = PaymentStatus.COMPLETED;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void markUsed() {
        this.status = PaymentStatus.USED;
    }
}
