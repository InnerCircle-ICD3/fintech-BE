package com.fastcampus.backofficemanage.dto.signup.request;

import com.fastcampus.backofficemanage.dto.signup.SignupRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "가맹점 회원가입 요청 DTO")
public class MerchantSignUpRequest extends SignupRequest {

    @NotBlank(message = "가맹점 이름은 필수입니다.")
    @Size(max = 50, message = "가맹점 이름은 최대 50자까지 입력 가능합니다.")
    @Schema(example = "패스트가맹점")
    private String name;

    @NotBlank(message = "사업자등록번호는 필수입니다.")
    @Size(max = 20, message = "사업자등록번호는 최대 20자까지 입력 가능합니다.")
    @Schema(example = "123-45-67890")
    private String businessNumber;

    @NotBlank(message = "담당자 이름은 필수입니다.")
    @Size(max = 30, message = "담당자 이름은 최대 30자까지 입력 가능합니다.")
    @Schema(example = "홍길동")
    private String contactName;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @Schema(example = "owner@example.com")
    private String contactEmail;

    @NotBlank(message = "연락처는 필수입니다.")
    @Size(max = 20, message = "연락처는 최대 20자까지 입력 가능합니다.")
    @Schema(example = "010-1234-5678")
    private String contactPhone;

    public MerchantSignUpRequest(String loginId, String loginPw,
                                 String name, String businessNumber,
                                 String contactName, String contactEmail, String contactPhone) {
        super(loginId, loginPw);
        this.name = name;
        this.businessNumber = businessNumber;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }
}
