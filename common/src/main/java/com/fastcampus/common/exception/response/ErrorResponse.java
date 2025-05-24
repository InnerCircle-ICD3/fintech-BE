package com.fastcampus.common.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "API 에러 응답")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "UNAUTHORIZED")
    private final String code;

    @Schema(description = "에러 메시지", example = "비밀번호가 일치하지 않습니다.")
    private final String message;

    @Schema(description = "HTTP 상태 코드", example = "401")
    private final int status;

    @Schema(description = "요청 경로", example = "/merchants/info")
    private final String path;

    @Schema(description = "에러 발생 시각", example = "2025-05-22T23:00:00")
    private final LocalDateTime timestamp;
}
