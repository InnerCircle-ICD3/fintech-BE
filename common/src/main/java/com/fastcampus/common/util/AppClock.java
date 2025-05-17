package com.fastcampus.common.util;

import java.time.Clock;
import java.time.ZoneId;

public class AppClock {
    public static final Clock CLOCK = Clock.system(ZoneId.of("Asia/Seoul"));
}