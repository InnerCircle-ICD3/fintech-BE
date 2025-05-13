package com.fastcampus.common.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZoneId;

@Getter
@Component
public class AppClock {
    private final Clock clock = Clock.system(ZoneId.of("Asia/Seoul"));
}