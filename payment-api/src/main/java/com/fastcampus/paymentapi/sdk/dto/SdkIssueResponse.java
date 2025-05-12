package com.fastcampus.paymentapi.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SdkIssueResponse {
    private String sdkKey;
    private LocalDateTime expiresAt;
}
