package com.kkokkomu.short_news.dto.newsReaction.request;

import com.kkokkomu.short_news.type.ENewsReaction;
import jakarta.validation.constraints.NotNull;

public record CreateNewsReactionDto(
        @NotNull Long newsId,
        @NotNull ENewsReaction reaction
) {
}
