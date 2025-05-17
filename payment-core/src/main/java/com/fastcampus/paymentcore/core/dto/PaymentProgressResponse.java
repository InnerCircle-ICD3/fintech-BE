package com.fastcampus.paymentcore.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentProgressResponse {
    /**
     * 결제 진행 응답 DTO
     * 결제 진행 후 클라이언트에게 전달하는 값
     * 결제 상태를 반환함
     */
    private String transactionToken; // 외부 공개용 거래 식별자
    private String status; //COMPLETED, FAILED, PENDING등

}
