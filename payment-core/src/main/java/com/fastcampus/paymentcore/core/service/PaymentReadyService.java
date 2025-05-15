package com.fastcampus.paymentcore.core.service;

import java.util.Map;

public interface PaymentReadyService {

    public abstract String readyPayment(Map<String, Object> paramMap);
}
