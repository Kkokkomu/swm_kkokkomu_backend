package com.kkokkomu.short_news.dto.userKeyword.request;

import jakarta.validation.constraints.NotNull;

public record CreateUserKeywordDto(
        @NotNull String keyword
) {
}
