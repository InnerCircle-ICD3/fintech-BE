package com.fastcampus.common.exception.code;

import com.fastcampus.common.exception.base.ErrorCode;
import org.springframework.http.HttpStatus;

public enum CardErrorCode implements ErrorCode {

    NOT_FOUND_CARD(HttpStatus.NOT_FOUND, "존재하지 않는 카드입니다."),
    UNAUTHORIZED_CARD_ACCESS(HttpStatus.UNAUTHORIZED, "해당 카드에 대한 접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

    CardErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
