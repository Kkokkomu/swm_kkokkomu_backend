package com.kkokkomu.short_news.user.dto.validateUser;

import jakarta.validation.constraints.NotNull;

public record EmailValicate(
        @NotNull String email
) {
}
