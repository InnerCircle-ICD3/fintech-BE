package com.fastcampus.paymentapi.initqr.dto;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.code.PaymentErrorCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class QrInitRequest {

    @NotBlank(message = "sdkKey는 필수입니다.")
    private String sdkKey;

    @NotNull(message = "merchantId는 필수입니다.")
    private Long merchantId;

    @NotNull(message = "merchantOrderId는 필수입니다.")
    private Long merchantOrderId;

    @NotNull
    @Min(value = 100, message = "최소 결제 금액은 100원입니다.")
    private Long amount;

    public void validateOrThrow() {
        if (merchantId == null || merchantOrderId == null || amount == null) {
            throw new HttpException(PaymentErrorCode.INVALID_PAYMENT_REQUEST);
        }
    }
}
