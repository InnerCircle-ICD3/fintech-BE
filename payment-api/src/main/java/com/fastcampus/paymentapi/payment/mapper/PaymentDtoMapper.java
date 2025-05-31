package com.fastcampus.paymentapi.payment.mapper;

import com.fastcampus.paymentapi.payment.dto.request.InitiateTransactionRequest;
import com.fastcampus.paymentapi.payment.dto.request.ApiProgressTransactionRequest;
import com.fastcampus.paymentcore.core.dto.PaymentReadyRequestDto;
import com.fastcampus.paymentcore.core.dto.ProgressTransactionRequest;

public class PaymentDtoMapper {

    public static PaymentReadyRequestDto toCoreDto(InitiateTransactionRequest request) {
        return new PaymentReadyRequestDto(
                request.getMerchantId(),
                request.getMerchantOrderId(),
                request.getAmount()
        );
    }

    public static ProgressTransactionRequest toCoreDto(ApiProgressTransactionRequest request) {
        return new ProgressTransactionRequest(request.getTransactionToken());
    }
}
