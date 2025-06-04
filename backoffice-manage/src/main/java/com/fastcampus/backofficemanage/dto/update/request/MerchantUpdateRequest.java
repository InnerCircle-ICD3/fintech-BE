package com.fastcampus.backofficemanage.dto.update.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "가맹점 정보 수정 요청 DTO")
public class MerchantUpdateRequest {

    @NotBlank(message = "가맹점 이름은 필수입니다.")
    @Size(max = 50, message = "가맹점 이름은 최대 50자까지 입력 가능합니다.")
    @Schema(example = "패스트업데이트상호명")
    private String name;

    @NotBlank(message = "사업자등록번호는 필수입니다.")
    @Size(max = 20, message = "사업자등록번호는 최대 20자까지 입력 가능합니다.")
    @Schema(example = "987-65-43210")
    private String businessNumber;

    @NotBlank(message = "담당자 이름은 필수입니다.")
    @Size(max = 30, message = "담당자 이름은 최대 30자까지 입력 가능합니다.")
    @Schema(example = "홍길동 대리")
    private String contactName;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    @Schema(example = "update@merchant.com")
    private String contactEmail;

    @NotBlank(message = "연락처는 필수입니다.")
    @Size(max = 20, message = "연락처는 최대 20자까지 입력 가능합니다.")
    @Schema(example = "010-2222-3333")
    private String contactPhone;
}
