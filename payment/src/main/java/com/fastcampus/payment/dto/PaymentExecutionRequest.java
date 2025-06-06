package com.fastcampus.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentExecutionRequest {

    @jakarta.validation.constraints.NotBlank
    private final String transactionToken;
    private final String cardToken;

}
