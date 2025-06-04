package com.fastcampus.payment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InitiateTransactionRequest {

    @NotBlank(message = "merchantId는 필수입니다.")
    private final String merchantId;

    @NotNull(message = "amount는 필수입니다.")
    private final Long amount;

    private final String orderId;

    @JsonCreator
    public InitiateTransactionRequest(
            @JsonProperty("merchantId") String merchantId,
            @JsonProperty("amount") Long amount,
            @JsonProperty("orderId") String orderId
    ) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.orderId = orderId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }
}