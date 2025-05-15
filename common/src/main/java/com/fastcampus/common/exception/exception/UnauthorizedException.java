package com.fastcampus.common.exception.exception;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.base.ErrorCode;

public class UnauthorizedException extends HttpException {
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
