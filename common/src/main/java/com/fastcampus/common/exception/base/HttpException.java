package com.fastcampus.common.exception.base;

import lombok.Getter;

@Getter
public class HttpException extends RuntimeException {

    private final ErrorCode errorCode;

    public HttpException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}