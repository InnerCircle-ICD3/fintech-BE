package com.fastcampus.paymentcore.core.service;

import com.fastcampus.paymentcore.core.dto.PaymentProgressRequest;
import com.fastcampus.paymentcore.core.dto.PaymentProgressResponse;

public interface PaymentExecutionService {
    PaymentProgressResponse execute(PaymentProgressRequest request);
}
