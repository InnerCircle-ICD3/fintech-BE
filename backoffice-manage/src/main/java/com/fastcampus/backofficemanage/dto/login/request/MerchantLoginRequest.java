package com.fastcampus.backofficemanage.dto.login.request;

import com.fastcampus.backofficemanage.dto.login.Login;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MerchantLoginRequest extends Login {

    @Builder
    public MerchantLoginRequest(String loginId, String loginPw) {
        super(loginId, loginPw);
    }
}
