package com.kkokkomu.short_news.news.dto.news.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record RequestGenerateNewsDto(
        @NotNull List<Integer> id_list,
        @NotNull int headline,
        @NotNull int politic,
        @NotNull int world,
        @NotNull int economy,
        @NotNull int IT,
        @NotNull int society,
        @NotNull int sports,
        @NotNull int entertain,
        @NotNull int culture
) {
}
