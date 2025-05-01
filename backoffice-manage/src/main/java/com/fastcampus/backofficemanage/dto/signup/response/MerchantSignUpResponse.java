package com.fastcampus.backofficemanage.dto.signup.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantSignUpResponse {

    private Long merchantId;
    private String loginId;
    private String name;
    private String status;
}
