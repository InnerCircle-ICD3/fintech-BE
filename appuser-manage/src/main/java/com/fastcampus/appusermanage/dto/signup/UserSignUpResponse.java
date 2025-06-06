package com.fastcampus.appusermanage.dto.signup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "유저 회원가입 응답")
public class UserSignUpResponse {

    private final Long userId;
    private final String email;
    private final String name;
    private final String status;

    public UserSignUpResponse(Long userId, String email, String name, String status) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
