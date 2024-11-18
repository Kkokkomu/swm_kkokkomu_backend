package com.kkokkomu.short_news.core.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class TimeUtil {
    public Boolean isNight() {
        LocalTime currentTime = LocalDateTime.now().toLocalTime();
        LocalTime startNightTime = LocalTime.of(21, 0); // 21:00 (밤 9시)
        LocalTime endNightTime = LocalTime.of(8, 0); // 08:00 (아침 8시)

        // 밤 시간은 저녁 9시부터 다음 날 아침 8시까지이므로 시간대 비교를 두 부분으로 나눕니다.
        return (currentTime.isAfter(startNightTime) || currentTime.equals(startNightTime))
                || currentTime.isBefore(endNightTime);
    }

    public Boolean isBetween8and10() {
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(10, 0);
        return now.isAfter(start) && now.isBefore(end);
    }
}
