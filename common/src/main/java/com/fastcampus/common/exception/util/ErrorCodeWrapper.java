package com.fastcampus.common.exception.util;

import com.fastcampus.common.exception.base.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 기존 ErrorCode의 상태와 이름은 유지하되,
 * 메시지만 커스터마이징할 수 있도록 래핑하는 유틸 클래스입니다.
 */
@Getter
public class ErrorCodeWrapper implements ErrorCode {

    private final String name;
    private final HttpStatus status;
    private final String message;

    private ErrorCodeWrapper(ErrorCode original, String customMessage) {
        this.name = original.name();
        this.status = original.getStatus();
        this.message = customMessage;
    }

    public static ErrorCode of(ErrorCode original, String customMessage) {
        return new ErrorCodeWrapper(original, customMessage);
    }

    @Override
    public String name() {
        return name;
    }
}
