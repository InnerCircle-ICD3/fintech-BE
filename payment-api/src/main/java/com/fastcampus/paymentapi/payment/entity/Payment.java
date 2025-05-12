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

    private Long merchantId;
    private Long merchantOrderId;
    private Long amount;

    private String qrUrl;

    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public Payment(String sdkKey, Long merchantId, Long merchantOrderId, Long amount, String qrUrl) {
        this.sdkKey = sdkKey;
        this.merchantId = merchantId;
        this.merchantOrderId = merchantOrderId;
        this.amount = amount;
        this.qrUrl = qrUrl;
        this.requestedAt = LocalDateTime.now();
        this.status = PaymentStatus.READY; // ✅ 초기 상태는 READY
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
