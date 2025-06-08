package com.fastcampus.payment.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class CommonUtil {


    @Value("${lifetime.qr}")
    private String ttlQr;

    @Value("${time.zoneId}")
    private String zoneId;

    public LocalDateTime generateExpiresAt() {
        // ttlQr가 초 단위로 들어오기 때문에 이를 Long으로 파싱
        long ttlInSeconds = Long.parseLong(ttlQr);

        // 주어진 시간대(zoneId)로 시스템 클락 생성
        Clock clock = Clock.system(ZoneId.of(zoneId));

        // 현재 시각에 ttlInSeconds만큼 더한 시각 계산
        return LocalDateTime.now(clock).plusSeconds(ttlInSeconds);
    }

    public Date convertToDate(LocalDateTime param) {
        // LocalDateTime을 ZonedDateTime으로 변환 (시간대 지정 필요)
        ZonedDateTime zonedDateTime = param.atZone(ZoneId.of(zoneId));

        // ZonedDateTime을 Instant로 변환 후, Date로 변환
        Date result = Date.from(zonedDateTime.toInstant());

        return result;
    }
}
