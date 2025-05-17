package com.fastcampus.common.exception.exception;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.base.ErrorCode;

public class NotFoundException extends HttpException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
