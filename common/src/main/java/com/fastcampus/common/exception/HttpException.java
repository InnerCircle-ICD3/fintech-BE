package com.fastcampus.common.exception;

import lombok.Getter;

@Getter
public class HttpException extends RuntimeException {

    private final ErrorCode errorCode;

    public HttpException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}