package com.fastcampus.appusermanage.dto.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdatePasswordRequest {
    private final String currentPassword;
    private final String newPassword;
}