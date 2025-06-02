package com.fastcampus.common.exception.exception;

import com.fastcampus.common.exception.base.ErrorCode;
import com.fastcampus.common.exception.base.HttpException;

public class BadRequestException extends HttpException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}