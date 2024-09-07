package com.kkokkomu.short_news.user.dto.user.request;

import jakarta.validation.constraints.NotNull;

public record BanUserDto(
        @NotNull int day
) {
}
