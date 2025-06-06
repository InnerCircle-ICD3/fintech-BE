package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.Transaction;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class PaymentReadyResponse {

    private final String transactionToken;
    private final LocalDateTime expireAt;

    public PaymentReadyResponse(Transaction transaction) {
        this.transactionToken = transaction.getTransactionToken();
        this.expireAt = transaction.getExpireAt();
    }



}
