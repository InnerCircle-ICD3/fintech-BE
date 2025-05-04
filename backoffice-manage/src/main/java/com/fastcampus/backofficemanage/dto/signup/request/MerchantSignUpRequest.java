package com.fastcampus.backofficemanage.dto.signup.request;

import com.fastcampus.backofficemanage.dto.signup.SignupRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MerchantSignUpRequest extends SignupRequest {

    private String name;
    private String businessNumber;
    private String contactName;
    private String contactEmail;
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
