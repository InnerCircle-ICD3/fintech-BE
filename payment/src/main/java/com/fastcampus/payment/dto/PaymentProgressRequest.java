package com.fastcampus.payment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter

public class PaymentProgressRequest {


    @NotBlank(message = "paymentToken 값은 필수입니다.")
    private final String paymentToken;

    @JsonCreator
    public PaymentProgressRequest(
            @JsonProperty("paymentToken") String paymentToken
    ) {
        this.paymentToken = paymentToken;
    }

}
