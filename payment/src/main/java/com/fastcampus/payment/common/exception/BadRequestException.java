package com.fastcampus.payment.common.exception;


import com.fastcampus.payment.common.exception.base.HttpException;
import com.fastcampus.payment.common.exception.error.PaymentErrorCode;

public class BadRequestException extends HttpException {
    public BadRequestException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
}