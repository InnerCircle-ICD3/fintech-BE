package com.fastcampus.payment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

public class PaymentProgressRequest {


    @NotBlank(message = "token 값은 필수입니다.")
    private final String token;

    @JsonCreator
    public PaymentProgressRequest(
            @JsonProperty("token") String token
    ) {
        this.token = token;
    }

}
