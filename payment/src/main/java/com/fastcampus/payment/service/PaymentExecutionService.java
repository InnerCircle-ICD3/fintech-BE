package com.fastcampus.payment.service;

import com.fastcampus.payment.dto.PaymentExecutionRequest;
import com.fastcampus.payment.dto.PaymentExecutionResponse;

public interface PaymentExecutionService {
    PaymentExecutionResponse execute(PaymentExecutionRequest request);
}
