package com.fastcampus.common.exception.exception;

import com.fastcampus.common.exception.base.HttpException;
import com.fastcampus.common.exception.base.ErrorCode;

public class DuplicateKeyException extends HttpException {

    public DuplicateKeyException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static DuplicateKeyException of(ErrorCode errorCode) {
        return new DuplicateKeyException(errorCode);
    }
}

