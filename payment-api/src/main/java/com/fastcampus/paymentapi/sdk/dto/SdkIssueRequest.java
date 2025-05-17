package com.fastcampus.paymentapi.sdk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SdkIssueRequest {
    @NotNull(message = "merchantId는 필수입니다.")
    private Long merchantId;
}