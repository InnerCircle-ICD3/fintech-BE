package com.fastcampus.paymentcore.core.dto;

import lombok.Data;

@Data
public class PaymentProgressRequest {
    /**
     * 결제 실행 요청 DTO
     * 클라이언트가 결제 실행할 때 전달하는 값
     */
    private String transactionToken; // 외부 공개용 거래 식별자
    private String cardToken; // 카드 식별용 (등록된 카드 token)

}
