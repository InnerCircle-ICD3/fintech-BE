package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentReadyRequest {

    @NotBlank(message = "merchantId는 필수입니다.")
    private final String merchantId;

    @NotNull(message = "amount는 필수입니다.")
    private final Long amount;

    private final String merchantOrderId;

    @JsonCreator
    public PaymentReadyRequest(
            @JsonProperty("merchantId") String merchantId,
            @JsonProperty("amount") Long amount,
            @JsonProperty("merchantOrderId") String merchantOrderId
    ) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.merchantOrderId = merchantOrderId;
    }

    public Transaction convertToTransaction() {
        Transaction transaction = new Transaction();
        transaction.setMerchantId(Long.valueOf(this.merchantId));
        transaction.setAmount(this.amount);
        transaction.setMerchantOrderId(this.merchantOrderId);
        return transaction;
    }

}
