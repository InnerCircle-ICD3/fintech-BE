package com.fastcampus.common.exception.customerror;

import com.fastcampus.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum SdkErrorCode implements ErrorCode {

    INVALID_SDK_KEY(HttpStatus.UNAUTHORIZED, "유효하지 않은 SDK 키입니다."),
    EXPIRED_SDK_KEY(HttpStatus.UNAUTHORIZED, "SDK 키가 만료되었습니다."),
    DUPLICATE_ORDER(HttpStatus.UNPROCESSABLE_ENTITY, "이미 처리된 주문입니다."),
    INVALID_MERCHANT(HttpStatus.BAD_REQUEST, "잘못된 가맹점입니다."),
    MERCHANT_SDK_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 가맹점은 이미 SDK 키를 보유하고 있습니다."),
    SDK_ISSUE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SDK 키 발급 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    SdkErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override public HttpStatus getStatus() { return status; }
    @Override public String getMessage() { return message; }
}
