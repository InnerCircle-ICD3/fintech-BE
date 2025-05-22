package com.fastcampus.paymentinfra.client;

import org.springframework.stereotype.Component;

@Component
public class MockCardApprovalClient {
    public boolean approve(String cardToken, Long amount) {
        return !cardToken.startsWith("Fail");
    }
}
