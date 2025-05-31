package com.fastcampus.paymentcore.core.dto;

public final class ProgressTransactionRequest {
    private final String transactionToken;

    public ProgressTransactionRequest(String transactionToken) {
        this.transactionToken = transactionToken;
    }

    public String getTransactionToken() {
        return transactionToken;
    }
}