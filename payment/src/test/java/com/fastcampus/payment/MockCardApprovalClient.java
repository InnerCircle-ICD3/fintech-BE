package com.fastcampus.payment;

import org.springframework.stereotype.Component;

@Component
public class MockCardApprovalClient {
    /**
     * 카드 토큰과 금액을 받아 결제 승인을 시뮬레이션합니다.
     *
     * 카드 토큰이 "Fail"로 시작하면 승인에 실패하여 {@code false}를 반환하고, 그렇지 않으면 {@code true}를 반환합니다.
     *
     * @param cardToken 결제 카드 토큰
     * @param amount 결제 금액
     * @return 결제 승인 여부
     */
    public boolean approve(String cardToken, Long amount) {
        return !cardToken.startsWith("Fail");
    }
}
