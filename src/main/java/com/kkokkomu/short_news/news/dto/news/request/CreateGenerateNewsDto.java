package com.kkokkomu.short_news.news.dto.news.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateGenerateNewsDto(
        @NotNull int headline,
        @NotNull int politic,
        @NotNull int world,
        @NotNull int economy,
        @NotNull int IT,
        @NotNull int society,
        @NotNull int sports,
        @NotNull int entertain,
        @NotNull int culture
){
}
