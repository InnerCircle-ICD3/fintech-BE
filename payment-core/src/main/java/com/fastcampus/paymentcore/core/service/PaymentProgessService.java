package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentcore.core.dto.PaymentProgressDto;

public interface PaymentProgessService {

    public abstract PaymentProgressDto progressPayment(String token);
}
