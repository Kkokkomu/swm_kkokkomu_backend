package com.kkokkomu.short_news.news.dto.news.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateGenerateNewsDto(
        @NotNull int count_news,
        @NotNull int count_sports,
        @NotNull int count_entertain
){
}
