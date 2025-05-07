package com.fastcampus.paymentcore.core.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyProcessed(CommonException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        String msg = "서버 오류가 발생했습니다.";
        msg = ex.getMessage(); // TODO - 개발 단계에서만 사용
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, msg);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                status.value(),
                message
        );
        return new ResponseEntity<>(error, status);
    }

    // 에러 응답 DTO
    public static class ErrorResponse {
        private final String timestamp;
        private final int status;
        private final String message;

        public ErrorResponse(String timestamp, int status, String message) {
            this.timestamp = timestamp;
            this.status = status;
            this.message = message;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

}
