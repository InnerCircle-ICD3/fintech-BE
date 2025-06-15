package com.fastcampus.appusermanage.dto.transaction;

import com.fastcampus.paymentmethod.entity.ReadOnlyPaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserTransactionResponse(

        @Schema(description = "거래 ID", example = "1001")
        @NotNull
        Long transactionId,

        @Schema(description = "결제 금액", example = "12000")
        @NotNull @Positive
        Long amount,

        @Schema(description = "거래 상태", example = "COMPLETED")
        @NotNull
        ReadOnlyPaymentStatus status,

        @Schema(description = "카드 토큰", example = "8b72aaca-3f6a-4ae4-8e5e-9f447d1b7f1a")
        String cardToken,

        @Schema(description = "거래 생성 시각", example = "2025-06-15T12:34:56")
        LocalDateTime createdAt
) {}
