package com.fastcampus.payment.servicedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.coyote.BadRequestException;

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

    /**
     * 필수 파라미터인 transactionToken 또는 cardToken이 null인지 검증합니다.
     *
     * 두 값 중 하나라도 null이면 BadRequestException을 PAYMENT_PROGRESS_NULL_VALUE 코드와 함께 발생시킵니다.
     *
     * @throws  BadRequestException 또는 cardToken이 null인 경우
     */
    public void nullCheckRequiredParam(){
        List<Object> targetList = Arrays.asList(transactionToken, cardToken);
        boolean isNull = targetList.stream().anyMatch(Objects::isNull);
        if (isNull){
            throw new RuntimeException("파라미터 내용을 확인해 주세요");
        }
    }


}
