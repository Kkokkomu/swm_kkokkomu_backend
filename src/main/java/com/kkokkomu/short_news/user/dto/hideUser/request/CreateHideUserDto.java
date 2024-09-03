package com.kkokkomu.short_news.user.dto.hideUser.request;

import jakarta.validation.constraints.NotNull;

public record CreateHideUserDto(
        @NotNull Long hidedUserId
) {
}
