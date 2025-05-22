package com.fastcampus.paymentcore.core.dto;

import com.fastcampus.paymentcore.core.dummy.TransactionEntityDummy;
import com.fastcampus.paymentinfra.entity.Transaction;
import com.fastcampus.paymentinfra.entity.TransactionStatus;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
public class PaymentProgressDto {

    private LocalDateTime createdAt;
    private String transactionToken;
    private TransactionStatus status;
    private Long amount;
    private String merchantOrderId;
    private Long merchantId;

    public PaymentProgressDto(Transaction transaction) {

        this.createdAt = transaction.getCreatedAt();
        this.transactionToken = transaction.getTransactionToken();
        this.status = transaction.getStatus();
        this.amount = transaction.getAmount();
        this.merchantOrderId = transaction.getMerchantOrderId();
        this.merchantId = transaction.getMerchantId();

    }
}
