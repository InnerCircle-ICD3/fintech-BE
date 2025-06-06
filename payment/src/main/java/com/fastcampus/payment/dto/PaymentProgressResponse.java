package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentProgressResponse {

    private final TransactionStatus status;
    private final Long amount;
    private final String merchantId;
    private final String merchantOrderId;
    private final LocalDateTime createdAt;

    public PaymentProgressResponse(Transaction transaction) {
        this.status = transaction.getStatus();
        this.amount = transaction.getAmount();
        this.createdAt = transaction.getCreatedAt();
        this.merchantId = Long.toString(transaction.getMerchantId());
        this.merchantOrderId = transaction.getMerchantOrderId();
    }

    public String getStatus() {
        return this.status.name();
    }
}
