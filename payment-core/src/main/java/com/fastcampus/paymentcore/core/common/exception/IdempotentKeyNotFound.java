package com.fastcampus.paymentcore.core.common.exception;

public class IdempotentKeyNotFound extends RuntimeException {

    public IdempotentKeyNotFound(String msg) {
        super(msg);
    }
}
