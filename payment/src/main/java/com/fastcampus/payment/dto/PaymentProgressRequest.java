package com.fastcampus.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentProgressRequest {

    @jakarta.validation.constraints.NotBlank
    private final String transactionToken;

}
