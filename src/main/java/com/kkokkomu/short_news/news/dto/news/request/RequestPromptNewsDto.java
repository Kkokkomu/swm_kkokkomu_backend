package com.kkokkomu.short_news.news.dto.news.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record RequestPromptNewsDto(
        @NotNull Integer id,
        @NotNull String section,
        @NotNull String url,
        @NotNull String content
) {
}
