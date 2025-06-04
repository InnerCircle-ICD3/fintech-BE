package com.fastcampus.paymentapi.payment.dto.response;

import com.fastcampus.paymentinfra.type.TransactionStatus;

/**
 * 결제 상태 응답 DTO (API 용)
 */
public final class PaymentProgressResponseDto {

    private final String transactionToken;
    private final TransactionStatus status;

    public PaymentProgressResponseDto(String transactionToken, TransactionStatus status) {
        this.transactionToken = transactionToken;
        this.status = status;
    }

    public String getTransactionToken() {
        return transactionToken;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}
