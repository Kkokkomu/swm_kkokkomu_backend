package com.kkokkomu.short_news.news.dto.news.request;

import com.kkokkomu.short_news.core.type.ECategory;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreatePromptNewsDto(
        @NotNull String content,
        @NotNull String section,
        @NotNull String url
        ){
}
