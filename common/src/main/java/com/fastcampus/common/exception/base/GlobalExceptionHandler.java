package com.fastcampus.common.exception.base;

import com.fastcampus.common.exception.code.CommonErrorCode;
import com.fastcampus.common.exception.exception.ValidationException;
import com.fastcampus.common.exception.response.ErrorResponse;
import com.fastcampus.common.exception.util.ErrorCodeWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ErrorResponse> handleHttpException(HttpException e, HttpServletRequest request) {
        log.warn("HttpException at {}: {}", request.getRequestURI(), e.getMessage());

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.builder()
                        .code(e.getErrorCode().name())
                        .message(e.getMessage())
                        .status(e.getErrorCode().getStatus().value())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleInvalid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(f -> f.getDefaultMessage())
                .orElse("잘못된 입력입니다.");

        throw new ValidationException(ErrorCodeWrapper.of(CommonErrorCode.VALIDATION_ERROR, message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraint(ConstraintViolationException e) {
        String message = e.getConstraintViolations()
                .stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("잘못된 파라미터입니다.");

        throw new ValidationException(ErrorCodeWrapper.of(CommonErrorCode.VALIDATION_ERROR, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), e.getMessage(), e);

        return ResponseEntity
                .status(CommonErrorCode.INTERNAL_ERROR.getStatus())
                .body(ErrorResponse.builder()
                        .code(CommonErrorCode.INTERNAL_ERROR.name())
                        .message(CommonErrorCode.INTERNAL_ERROR.getMessage())
                        .status(CommonErrorCode.INTERNAL_ERROR.getStatus().value())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
