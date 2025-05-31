package com.fastcampus.paymentapi.payment.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ApiProgressTransactionRequest {

    @NotBlank
    private final String transactionToken;

    public ApiProgressTransactionRequest(String transactionToken) {
        this.transactionToken = transactionToken;
    }

    public String getTransactionToken() {
        return transactionToken;
    }
}