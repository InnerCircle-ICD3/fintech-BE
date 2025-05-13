package com.fastcampus.backoffice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiKeyDto {
    private Long id;
    private String key;
    private String secret;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
} 