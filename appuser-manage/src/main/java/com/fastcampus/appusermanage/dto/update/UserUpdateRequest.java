package com.fastcampus.appusermanage.dto.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    private final String name;
    private final String phone;
}
