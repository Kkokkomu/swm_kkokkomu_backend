package com.kkokkomu.short_news.dto.newsReaction.response;

import com.kkokkomu.short_news.domain.News;
import lombok.Builder;

@Builder
public record ReactionCntDto(
        Long like,
        Long surprise,
        Long sad,
        Long angry
) {
}
