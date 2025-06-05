package com.fastcampus.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentProgressResponse {


    private final String status;
    private final Long amount;
    private final String merchantId;
    private final String merchantOrderId;
    private final LocalDateTime createdAt;
}
