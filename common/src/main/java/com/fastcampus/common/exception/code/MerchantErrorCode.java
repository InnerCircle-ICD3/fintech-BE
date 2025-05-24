package com.fastcampus.common.exception.code;

import com.fastcampus.common.exception.base.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MerchantErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 가맹점입니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 로그인 ID입니다."),
    DUPLICATE_BUSINESS_NUMBER(HttpStatus.CONFLICT, "이미 존재하는 사업자등록번호입니다."),
    DUPLICATE_API_KEY(HttpStatus.CONFLICT, "이미 활성화된 API 키가 존재합니다.");

    private final HttpStatus status;
    private final String message;

    MerchantErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override public HttpStatus getStatus() { return status; }
    @Override public String getMessage() { return message; }
}
