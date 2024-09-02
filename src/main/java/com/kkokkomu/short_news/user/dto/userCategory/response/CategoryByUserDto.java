package com.kkokkomu.short_news.user.dto.userCategory.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CategoryByUserDto(
        Long userId,
        Boolean politics,
        Boolean economy,
        Boolean social,
        Boolean entertain,
        Boolean sports,
        Boolean living,
        Boolean world,
        Boolean it
) {
}
