package com.fastcampus.common.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;
    private final int status;
    private final String path;
    private final LocalDateTime timestamp;
}
