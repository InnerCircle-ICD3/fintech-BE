package com.fastcampus.backofficemanage.dto.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
@Schema(description = "공통 수정 요청 필드")
public abstract class UpdateRequest {

    @NotBlank(message = "로그인 ID는 필수입니다.")
    @Size(max = 20, message = "로그인 ID는 최대 20자까지 입력 가능합니다.")
    @Schema(description = "로그인 ID", example = "merchant01")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 20, message = "비밀번호는 최대 20자까지 입력 가능합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{1,20}$",
            message = "비밀번호는 특수문자, 영어 대/소문자, 숫자를 각각 포함해야 하며 최대 20자입니다."
    )
    @Schema(description = "로그인 비밀번호", example = "Passw0rd!")
    private String loginPw;
}
