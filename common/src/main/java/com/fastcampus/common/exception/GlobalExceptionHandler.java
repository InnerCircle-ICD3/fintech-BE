package com.fastcampus.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException e, HttpServletRequest request) {
        log.warn("BaseException at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(e.getErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException e, HttpServletRequest request) {
        log.warn("IllegalArgumentException at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(new ErrorResponse(ErrorCode.INVALID_REQUEST.name(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(new ErrorResponse(ErrorCode.INTERNAL_ERROR.name(), ErrorCode.INTERNAL_ERROR.getMessage()));
    }
}
