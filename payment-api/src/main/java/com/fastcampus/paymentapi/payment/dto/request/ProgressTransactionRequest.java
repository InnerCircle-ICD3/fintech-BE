package com.fastcampus.paymentapi.payment.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ProgressTransactionRequest {

    @jakarta.validation.constraints.NotBlank
    private final String token;

    public ProgressTransactionRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}