package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.Payment;
import com.fastcampus.payment.entity.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentReadyRequest {

    @NotBlank(message = "merchantId는 필수입니다.")
    private final Long merchantId;

    @NotNull(message = "amount는 필수입니다.")
    private final Long amount;

    private final String merchantOrderId;

    @JsonCreator
    public PaymentReadyRequest(
            @JsonProperty("amount") Long amount,
            @JsonProperty("merchantId") Long merchantId,
            @JsonProperty("merchantOrderId") String merchantOrderId
    ) {
        this.amount = amount;
        this.merchantId = merchantId;
        this.merchantOrderId = merchantOrderId;
    }

    public Payment convertToPayment() {
        Payment payment = new Payment();
        payment.setTotalAmount(this.amount);
        payment.setMerchantOrderId(this.merchantOrderId);
        payment.setMerchantId(this.merchantId);
        payment.setStatus(PaymentStatus.READY);
        return payment;
    }

}
