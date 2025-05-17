package com.fastcampus.paymentcore.core.dto;

import lombok.Data;

@Data
public class IdempotencyDto {
    public String responseData;
    public int id;
}
