package com.fastcampus.appusermanage.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 로그인 요청")
public class UserLoginRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Size(max = 50, message = "이메일은 최대 50자까지 입력 가능합니다.")
    @Schema(description = "로그인 이메일", example = "user@email.com")
    private final String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 20, message = "비밀번호는 최대 20자까지 입력 가능합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{8,20}$",
            message = "비밀번호는 특수문자, 영어 소문자, 숫자를 각각 최소 하나씩 포함해야 하며 8자 이상 20자 이하여야 합니다."
    )
    @Schema(description = "로그인 비밀번호", example = "passw0rd!")
    private final String password;

    public UserLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter만!
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
