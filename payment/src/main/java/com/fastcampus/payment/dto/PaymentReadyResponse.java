package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.Payment;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class PaymentReadyResponse {

    private final String paymentToken;
    private final LocalDateTime expireAt;

    public PaymentReadyResponse(Payment payment) {
        //
        if(payment == null) {
            throw new IllegalArgumentException("PaymentReadyResponse > payment 가 비어 있습니다");
        }
        this.paymentToken = payment.getPaymentToken();
        //
        if(payment.getLastTransaction() == null) {
            throw new IllegalArgumentException("PaymentReadyResponse > payment.getLastTransaction() 가 비어 있습니다");
        }
        this.expireAt = payment.getLastTransaction().getExpireAt();
    }



}
