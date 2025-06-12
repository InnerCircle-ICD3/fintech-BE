package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.Payment;
import lombok.Getter;



@Getter
public class PaymentCancelResponse {

    private final String paymentToken;
    private final String status;
    private final String merchantOrderId;

    public PaymentCancelResponse(Payment payment) {
        this.status = payment.getStatus().toString();
        this.paymentToken = payment.getPaymentToken();
        this.merchantOrderId = payment.getMerchantOrderId();
    }

}
