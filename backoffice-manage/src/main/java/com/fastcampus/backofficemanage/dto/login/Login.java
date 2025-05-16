package com.fastcampus.backofficemanage.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Login {
    private String loginId;
    private String loginPw;
}
