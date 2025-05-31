package com.fastcampus.paymentcore.core.dto;

public final class PaymentReadyRequestDto {
    private final Long merchantId;
    private final String merchantOrderId;
    private final Long amount;

    public PaymentReadyRequestDto(Long merchantId, String merchantOrderId, Long amount) {
        this.merchantId = merchantId;
        this.merchantOrderId = merchantOrderId;
        this.amount = amount;
    }

    public Long merchantId() {
        return merchantId;
    }

    public String merchantOrderId() {
        return merchantOrderId;
    }

    public Long amount() {
        return amount;
    }
}
