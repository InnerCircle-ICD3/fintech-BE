package com.fastcampus.backofficemanage.dto.login.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantLoginResponse {
    private String accessToken;
    private String refreshToken;
}
