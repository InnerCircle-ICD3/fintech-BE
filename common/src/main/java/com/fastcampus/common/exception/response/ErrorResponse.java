package com.fastcampus.common.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "에러 코드")
    private final String code;

    @Schema(description = "에러 메시지")
    private final String message;

    @Schema(description = "HTTP 상태 코드")
    private final int status;

    @Schema(description = "요청 경로")
    private final String path;

    @Schema(description = "에러 발생 시각")
    private final LocalDateTime timestamp;
}