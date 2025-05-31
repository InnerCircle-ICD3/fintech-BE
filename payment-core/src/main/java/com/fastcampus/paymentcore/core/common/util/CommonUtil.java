package com.fastcampus.paymentcore.core.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class CommonUtil {


    @Value("${lifetime.qr}")
    private String ttlQr;

    @Value("${time.zoneId}")
    private String zoneId;

    public LocalDateTime generateExpiresAt() {
        Clock clock = Clock.system(ZoneId.of(zoneId));
        return LocalDateTime.now(clock).plusSeconds(Integer.parseInt(ttlQr));
    }

}
