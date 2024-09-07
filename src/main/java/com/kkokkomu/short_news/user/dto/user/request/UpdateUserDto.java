package com.kkokkomu.short_news.user.dto.user.request;

import com.kkokkomu.short_news.core.type.ESex;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateUserDto(
        @NotNull String nickname,
        @NotNull LocalDate birthday,
        @NotNull ESex sex
) {
}
