package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.CardInfo;
import com.fastcampus.payment.entity.PaymentMethod;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentExecuteResponse {

    private final String transactionToken;
    private final TransactionStatus status;

    private final Long amount;
    private final String merchantId;
    private final String merchantOrderId;
    private final LocalDateTime createdAt;

    //  추가 필드들

    private final CardInfo cardInfo;
    private final PaymentMethod paymentMethod;
    private final Boolean approvalResult;

    public PaymentExecuteResponse(Transaction transaction, CardInfo cardInfo, PaymentMethod paymentMethod, Boolean approvalResult) {
        this.status = transaction.getStatus();
        this.amount = transaction.getAmount();
        this.createdAt = transaction.getCreatedAt();
        this.merchantId = Long.toString(transaction.getMerchantId());
        this.merchantOrderId = transaction.getMerchantOrderId();
        this.transactionToken = transaction.getTransactionToken();
        this.cardInfo = cardInfo;
        this.paymentMethod = paymentMethod;
        this.approvalResult = approvalResult;
    }


}
