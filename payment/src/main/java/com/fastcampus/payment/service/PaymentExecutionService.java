package com.fastcampus.payment.service;

import com.fastcampus.payment.servicedto.PaymentProgressRequest;
import com.fastcampus.payment.servicedto.PaymentProgressResponse;

public interface PaymentExecutionService {
    PaymentProgressResponse execute(PaymentProgressRequest request);
}
