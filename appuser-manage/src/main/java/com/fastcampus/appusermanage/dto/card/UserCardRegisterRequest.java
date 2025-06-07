package com.fastcampus.appusermanage.dto.card;

import com.fastcampus.appusermanage.entity.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserCardRegisterRequest {

    @NotBlank(message = "카드 번호는 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{4}-\\d{4}-\\d{4}", message = "카드 번호 형식이 올바르지 않습니다.")
    private final String cardNumber;

    @NotBlank(message = "유효기간은 필수입니다.")
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "유효기간은 MM/YY 형식이어야 합니다.")
    private final String expiryDate;

    @NotBlank(message = "생년월일은 필수입니다.")
    @Pattern(regexp = "\\d{6}", message = "생년월일 형식이 올바르지 않습니다.")
    private final String birthDate;

    @NotBlank(message = "카드 비밀번호 앞 두 자리는 필수입니다.")
    @Pattern(regexp = "\\d{2}", message = "카드 비밀번호 앞 두 자리는 무조건 2자리 숫자입니다.")
    private final String cardPw;

    @NotBlank(message = "CVC는 필수입니다.")
    @Pattern(regexp = "\\d{3,4}", message = "CVC는 3~4자리 숫자입니다.")
    private final String cvc;

    @NotBlank(message = "결제 비밀번호는 필수입니다.")
    @Pattern(regexp = "\\d{6}", message = "결제 비밀번호는 6자리 숫자입니다.")
    private final String paymentPassword;

    @NotBlank(message = "카드 회사는 필수입니다.")
    private final String cardCompany;

    @NotNull(message = "카드 타입은 필수입니다.")
    private final CardType type;

    public UserCardRegisterRequest(String cardNumber,
                                   String expiryDate,
                                   String birthDate,
                                   String cardPw,
                                   String cvc,
                                   String paymentPassword,
                                   String cardCompany,
                                   CardType type) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.birthDate = birthDate;
        this.cardPw = cardPw;
        this.cvc = cvc;
        this.paymentPassword = paymentPassword;
        this.cardCompany = cardCompany;
        this.type = type;
    }
}
