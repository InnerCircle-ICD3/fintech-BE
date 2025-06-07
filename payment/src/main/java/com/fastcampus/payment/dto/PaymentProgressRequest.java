package com.fastcampus.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class PaymentProgressRequest {

    private String cardToken;
    private String paymentMethodType;


    @jakarta.validation.constraints.NotBlank
    private final String transactionToken;

    public PaymentProgressRequest(String transactionToken) {
        this.transactionToken = transactionToken;
    }

    public void nullCheckRequiredParam(){
        if(transactionToken == null || transactionToken.trim().isEmpty()){
            throw new IllegalArgumentException("transactionToken은 필수값입니다.");
        }
    }
}
