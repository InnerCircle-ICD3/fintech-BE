package com.fastcampus.payment.common.exception.error;


import com.fastcampus.payment.common.exception.base.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorCode {

    DUPLICATE_ORDER(HttpStatus.UNPROCESSABLE_ENTITY, "이미 처리된 주문입니다."),
    INVALID_PAYMENT_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 결제 요청입니다."),
    PAYMENT_READY_NULL_VALUE(HttpStatus.BAD_REQUEST, "필수값입니다: merchantId, merchantOrderId, transactionToken"),
    QR_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QR URL 생성에 실패했습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 결제 정보를 찾을 수 없습니다."),
    PAYMENT_EXPIRED(HttpStatus.GONE, "결제 유효 시간이 만료되었습니다."),

    PAYMENT_EXECUTION_NULL_VALUE(HttpStatus.BAD_REQUEST, "필수값입니다: token, cardToken, paymentMethodType"), // 추가


    // code 정보 관련 error
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "카드 정보를 찾을 수 없습니다."),
    PAYMENT_METHOD_NOT_FOUND(HttpStatus.BAD_REQUEST, "결제 수단을 찾을 수 없습니다."),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "유효하지 않은 결제 수단입니다."),
    CARD_APPROVAL_FAILED(HttpStatus.PAYMENT_REQUIRED, "카드 승인이 거절되었습니다."),
    INACTIVE_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "비활성화된 결제 수단입니다."),
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
