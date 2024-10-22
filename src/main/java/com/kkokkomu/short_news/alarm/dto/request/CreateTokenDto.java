package com.kkokkomu.short_news.alarm.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateTokenDto (
        @NotNull
        String fcmToken,

        @NotNull
        String deviceId
) {
}
