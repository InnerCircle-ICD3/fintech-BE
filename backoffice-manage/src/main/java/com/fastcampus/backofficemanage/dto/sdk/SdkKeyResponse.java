package com.fastcampus.backofficemanage.dto.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SDK Key 조회 응답 DTO")
public class SdkKeyResponse {

    @Schema(description = "가맹점의 SDK Key", example = "b9b5bc33-38f7-466f-bfaf-1234567890ab")
    private String sdkKey;
}