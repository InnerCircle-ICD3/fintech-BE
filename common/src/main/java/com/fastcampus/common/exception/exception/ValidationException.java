package com.fastcampus.common.exception.exception;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.base.ErrorCode;

public class ValidationException extends HttpException {
    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
