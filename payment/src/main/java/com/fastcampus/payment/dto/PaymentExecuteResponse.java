package com.fastcampus.payment.dto;

import com.fastcampus.payment.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentExecuteResponse {

    private final String transactionToken;
    private final TransactionStatus status;

}
