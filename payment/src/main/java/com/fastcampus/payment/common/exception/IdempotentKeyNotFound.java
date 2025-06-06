package com.fastcampus.payment.common.exception;

public class IdempotentKeyNotFound extends RuntimeException {

    public IdempotentKeyNotFound(String msg) {
        super(msg);
    }
}
