package com.kkokkomu.short_news.user.dto.user.request;

import jakarta.validation.constraints.NotNull;

public record UpdateAlarmSettingDto(
        @NotNull Boolean nightAlarmYn,
        @NotNull Boolean alarmNewContentYn,
        @NotNull Boolean alarmReplyYn,
        @NotNull Boolean alarmBannedYn
) {
}
