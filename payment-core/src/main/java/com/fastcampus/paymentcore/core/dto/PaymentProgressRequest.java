package com.fastcampus.paymentcore.core.dto;

import com.fastcampus.common.exception.code.PaymentErrorCode;
import com.fastcampus.common.exception.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class PaymentProgressRequest {
    /**
     * 결제 실행 요청 DTO
     * 클라이언트가 결제 실행할 때 전달하는 값
     */
    private String transactionToken; // 외부 공개용 거래 식별자
    private String cardToken; // 카드 식별용 (등록된 카드 token)

    //validation methods
    public void nullCheckRequiredParam(){
        List<Object> targetList = Arrays.asList(transactionToken, cardToken);
        boolean isNull = targetList.stream().anyMatch(Objects::isNull);
        if (isNull){
            throw new BadRequestException(PaymentErrorCode.PAYMENT_PROGRESS_NULL_VALUE);
        }
    }


}
