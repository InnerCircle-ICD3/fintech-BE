package com.fastcampus.paymentapi.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InitiateTransactionRequest {

    @NotBlank(message = "merchantId는 필수입니다.")
    private final Long merchantId;

    @NotNull(message = "amount는 필수입니다.")
    private final Long amount;

    @NotBlank(message = "merchantOrderId는 필수입니다.")
    private final String merchantOrderId;

    @JsonCreator
    public InitiateTransactionRequest(
            @JsonProperty("merchantId") Long merchantId,
            @JsonProperty("amount") Long amount,
            @JsonProperty("merchantOrderId") String merchantOrderId
    ) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.merchantOrderId = merchantOrderId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }
}
