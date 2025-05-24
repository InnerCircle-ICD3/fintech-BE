package com.fastcampus.paymentcore.core.dto;

import lombok.Data;

@Data
public class IdempotencyDto {
    private int id;
    private String idempotencyKey;
    private String responseData;
}
