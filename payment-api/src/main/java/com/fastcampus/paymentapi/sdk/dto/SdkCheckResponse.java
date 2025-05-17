package com.fastcampus.paymentapi.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SdkCheckResponse {
    private boolean valid;
    private Long merchantId;
}