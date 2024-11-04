package com.kkokkomu.short_news.alarm.dto.notification.request;

import jakarta.validation.constraints.NotNull;

public record CreateNotificationDto(
        @NotNull String title,
        @NotNull String body
) {
}
