package com.kkokkomu.short_news.news.dto.news.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record RequestGenerateNewsDto(
        @NotNull List<Integer> id_list,
        @NotNull int count_news,
        @NotNull int count_sports,
        @NotNull int count_entertain
) {
}
