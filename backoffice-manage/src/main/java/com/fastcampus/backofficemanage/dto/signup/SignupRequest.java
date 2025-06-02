package com.fastcampus.backofficemanage.dto.signup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "공통 회원가입 요청 필드")
public class SignupRequest {

    @NotBlank(message = "로그인 ID는 필수입니다.")
    @Size(max = 20, message = "로그인 ID는 최대 20자까지 입력 가능합니다.")
    @Schema(description = "로그인 ID", example = "merchant01")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 20, message = "비밀번호는 최대 20자까지 입력 가능합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{1,20}$",
            message = "비밀번호는 특수문자, 영어 대/소문자, 숫자를 각각 최소 하나씩 포함해야 하며 20자 이하여야 합니다."
    )
    @Schema(description = "로그인 비밀번호", example = "Passw0rd!")
    private String loginPw;
}
