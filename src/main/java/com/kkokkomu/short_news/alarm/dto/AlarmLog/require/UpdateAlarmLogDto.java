package com.kkokkomu.short_news.alarm.dto.AlarmLog.require;

import java.util.List;

public record UpdateAlarmLogDto(
        List<Long> alarmLogIds
) {
}
