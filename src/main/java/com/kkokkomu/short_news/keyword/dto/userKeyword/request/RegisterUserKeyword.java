package com.kkokkomu.short_news.keyword.dto.userKeyword.request;

import jakarta.validation.constraints.NotNull;

public record RegisterUserKeyword(
        @NotNull Long keywordId
) {
}
