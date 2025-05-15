package com.fastcampus.common.exception.code;

import com.fastcampus.common.exception.base.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements ErrorCode {

    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 로그인 ID입니다."),
    NOT_FOUND_ID(HttpStatus.NOT_FOUND, "존재하지 않는 ID입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    MISSING_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 없습니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "리프레시 토큰이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");

    private final HttpStatus status;
    private final String message;

    AuthErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override public HttpStatus getStatus() { return status; }
    @Override public String getMessage() { return message; }
}
