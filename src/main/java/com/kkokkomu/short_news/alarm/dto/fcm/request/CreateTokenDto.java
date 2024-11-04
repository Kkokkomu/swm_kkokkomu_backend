package com.kkokkomu.short_news.alarm.dto.fcm.request;

import jakarta.validation.constraints.NotNull;

public record CreateTokenDto (
        @NotNull
        String fcmToken
) {
}
