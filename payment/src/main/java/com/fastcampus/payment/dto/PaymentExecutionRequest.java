package com.fastcampus.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor   // 🔥 기본 생성자 추가 (테스트에서 setter 사용하려면 필요)
@AllArgsConstructor  // 🔥 전체 생성자
public class PaymentExecutionRequest {

    @jakarta.validation.constraints.NotBlank
    private String transactionToken;

    @jakarta.validation.constraints.NotBlank  // 🔥 검증 추가
    private String cardToken;

    @jakarta.validation.constraints.NotBlank  // 🔥 검증 추가
    private String paymentMethodType;  // 🔥 필드 추가!

    // 🔥 검증 메서드 추가
    public void nullCheckRequiredParam() {
        if (transactionToken == null || transactionToken.trim().isEmpty()) {
            throw new IllegalArgumentException("transactionToken은 필수입니다.");
        }
        if (cardToken == null || cardToken.trim().isEmpty()) {
            throw new IllegalArgumentException("cardToken은 필수입니다.");
        }
        if (paymentMethodType == null || paymentMethodType.trim().isEmpty()) {
            throw new IllegalArgumentException("paymentMethodType은 필수입니다.");
        }
    }
}
