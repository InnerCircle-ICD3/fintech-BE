package com.fastcampus.paymentcore.core.dto;

import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.entity.TransactionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentProgressDto {

    private final LocalDateTime createdAt;
    private final String transactionToken;
    private final TransactionStatus status;
    private final Long amount;
    private final String merchantOrderId;
    private final Long merchantId;

    public PaymentProgressDto(Transaction transaction) {
        this.createdAt = transaction.getCreatedAt();
        this.transactionToken = transaction.getTransactionToken();
        this.status = transaction.getStatus();
        this.amount = transaction.getAmount();
        this.merchantOrderId = transaction.getMerchantOrderId();
        this.merchantId = transaction.getMerchantId();
    }
}
