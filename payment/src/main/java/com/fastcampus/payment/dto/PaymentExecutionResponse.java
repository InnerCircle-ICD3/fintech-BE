package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PaymentExecutionResponse {

    private final String token;
    private final PaymentStatus status;

    private final Long amount;
    private final String merchantId;
    private final String merchantOrderId;
    private final LocalDateTime createdAt;

    //  추가 필드들

    private final CardInfo cardInfo;
    private final PaymentMethod paymentMethod;
    private final Boolean approvalResult;

    public PaymentExecutionResponse(Payment payment, CardInfo cardInfo, PaymentMethod paymentMethod, Boolean approvalResult) {
        Transaction transaction =  payment.getLastTransaction();
        this.merchantId = Long.toString(payment.getMerchantId());
        this.merchantOrderId = payment.getMerchantOrderId();
        this.token = payment.getToken();
        this.status = payment.getStatus();
        this.amount = transaction.getAmount();
        this.createdAt = transaction.getCreatedAt();
        this.cardInfo = cardInfo;
        this.paymentMethod = paymentMethod;
        this.approvalResult = approvalResult;
    }


}
