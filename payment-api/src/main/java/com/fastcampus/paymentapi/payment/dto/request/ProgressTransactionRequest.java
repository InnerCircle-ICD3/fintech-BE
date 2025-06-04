package com.fastcampus.paymentapi.payment.dto.request;


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