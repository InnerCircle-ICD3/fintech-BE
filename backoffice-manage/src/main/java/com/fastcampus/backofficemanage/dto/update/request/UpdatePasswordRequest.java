package com.fastcampus.backofficemanage.dto.update.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 변경 요청 DTO")
public class UpdatePasswordRequest {

    @NotBlank(message = "기존 비밀번호는 필수입니다.")
    @Schema(description = "현재 비밀번호", example = "oldPass!1")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "새 비밀번호는 8~20자입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[^a-z\\d]).{8,20}$",
            message = "특수문자, 영어 소문자, 숫자를 각각 포함해야 합니다."
    )
    @Schema(description = "새 비밀번호", example = "NewPass123!")
    private String newPassword;
}
