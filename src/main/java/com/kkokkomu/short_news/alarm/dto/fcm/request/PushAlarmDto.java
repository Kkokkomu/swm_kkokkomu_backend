package com.kkokkomu.short_news.alarm.dto.fcm.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PushAlarmDto(
        @NotNull String fcmToken,
        @NotNull String title,
        @NotNull String body
) {
}
