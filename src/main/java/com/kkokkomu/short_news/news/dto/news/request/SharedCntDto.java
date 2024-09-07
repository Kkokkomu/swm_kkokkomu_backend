package com.kkokkomu.short_news.news.dto.news.request;

import jakarta.validation.constraints.NotNull;

public record SharedCntDto(
        @NotNull Long newsId
) {
}
