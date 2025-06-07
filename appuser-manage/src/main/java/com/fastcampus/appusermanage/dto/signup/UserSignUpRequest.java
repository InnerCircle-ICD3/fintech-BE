package com.fastcampus.appusermanage.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 회원가입 요청")
public class UserSignUpRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 50, message = "이메일은 최대 50자까지 입력 가능합니다.")
    @Schema(description = "회원가입 이메일", example = "user@email.com")
    private final String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).+$",
            message = "비밀번호는 특수문자, 영어 소문자, 숫자를 각각 최소 하나씩 포함해야 합니다."
    )
    @Schema(description = "비밀번호", example = "passw0rd!")
    private final String password;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 10, message = "이름은 최대 10자까지 입력 가능합니다.")
    @Schema(description = "이름", example = "홍길동")
    private final String name;

    @Size(max = 20, message = "전화번호는 최대 20자까지 입력 가능합니다.")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private final String phone;

    public UserSignUpRequest(String email, String password, String name, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
