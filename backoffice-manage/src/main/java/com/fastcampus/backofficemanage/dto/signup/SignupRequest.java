package com.fastcampus.backofficemanage.dto.signup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class SignupRequest {
    private String loginId;
    private String loginPw;
}