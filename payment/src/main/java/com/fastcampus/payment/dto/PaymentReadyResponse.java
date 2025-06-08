package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.Payment;
import com.fastcampus.payment.entity.Transaction;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class PaymentReadyResponse {

    private final String token;
    private final LocalDateTime expireAt;

    public PaymentReadyResponse(Payment payment) {
        this.token = payment.getToken();
        this.expireAt = payment.getLastTransaction().getExpireAt();
    }



}
