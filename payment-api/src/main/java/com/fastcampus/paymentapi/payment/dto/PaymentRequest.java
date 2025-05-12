package com.fastcampus.paymentapi.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentRequest {
    @NotBlank(message = "sdkKey는 필수입니다.")
    private String sdkKey;

    @NotNull(message = "merchantId는 필수입니다.")
    private Long merchantId;

    @NotNull(message = "merchantOrderId는 필수입니다.")
    private Long merchantOrderId;

    @NotNull
    @Min(value = 100, message = "최소 결제 금액은 100원입니다.")
    private Long amount;
}