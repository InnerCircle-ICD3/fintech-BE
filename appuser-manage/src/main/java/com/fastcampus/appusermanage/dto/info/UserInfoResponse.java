package com.fastcampus.appusermanage.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private final String email;
    private final String name;
    private final String phone;
    private final String status;
}
