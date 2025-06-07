package com.fastcampus.appusermanage.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "유저 로그인 응답")
public class UserLoginResponse {

    private final String accessToken;
    private final String refreshToken;

    public UserLoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
