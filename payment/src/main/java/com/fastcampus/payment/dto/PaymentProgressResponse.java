package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.Payment;
import com.fastcampus.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class PaymentProgressResponse {

    private final PaymentStatus status;
    private final Long amount;
    private final Long merchantId;
    private final String merchantOrderId;
    private final LocalDateTime createdAt;
    private String paymentToken;

    public PaymentProgressResponse(Payment payment) {
        this.status = payment.getStatus();
        this.amount = payment.getTotalAmount();
        this.createdAt = payment.getCreatedAt();
        this.merchantId = payment.getMerchantId();
        this.merchantOrderId = payment.getMerchantOrderId();
        this.paymentToken = payment.getPaymentToken();
    }

    public String getStatus() {
        return this.status.name();
    }

}
