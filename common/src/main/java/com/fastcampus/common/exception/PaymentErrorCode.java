package com.fastcampus.common.exception;

import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorCode {

    DUPLICATE_ORDER(HttpStatus.UNPROCESSABLE_ENTITY, "이미 처리된 주문입니다."),
    INVALID_PAYMENT_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 결제 요청입니다."),
    QR_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QR URL 생성에 실패했습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 결제 정보를 찾을 수 없습니다."),
    PAYMENT_EXPIRED(HttpStatus.GONE, "결제 유효 시간이 만료되었습니다."),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 결제가 완료된 주문입니다.");

    private final HttpStatus status;
    private final String message;

    PaymentErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override public HttpStatus getStatus() { return status; }
    @Override public String getMessage() { return message; }
}
