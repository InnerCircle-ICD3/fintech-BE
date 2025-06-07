package com.fastcampus.appusermanage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonResponse {

    private final boolean success;
    private final String message;

    public static CommonResponse success(String message) {
        return CommonResponse.builder()
                .success(true)
                .message(message)
                .build();
    }

    public static CommonResponse failure(String message) {
        return CommonResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
