package com.fastcampus.common.exception.exception;

import com.fastcampus.common.exception.base.ErrorCode;
import com.fastcampus.common.exception.base.HttpException;

public class ForbiddenException extends HttpException {
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}