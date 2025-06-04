package com.fastcampus.payment.dto;

import java.time.LocalDateTime;

public class GetTransactionProgressResponse {

    private final String transactionToken;
    private final String status;
    private final Long amount;
    private final LocalDateTime createdAt;

    public GetTransactionProgressResponse(String transactionToken, String status, Long amount, LocalDateTime createdAt) {
        this.transactionToken = transactionToken;
        this.status = status;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public String getTransactionToken() {
        return transactionToken;
    }

    public String getStatus() {
        return status;
    }

    public Long getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}