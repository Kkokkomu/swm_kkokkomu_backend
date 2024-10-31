package com.kkokkomu.short_news.alarm.dto.request;

import jakarta.validation.constraints.NotNull;

public record PushAlarmDto(
        @NotNull String fcmToken,
        @NotNull String title,
        @NotNull String body
) {
}
