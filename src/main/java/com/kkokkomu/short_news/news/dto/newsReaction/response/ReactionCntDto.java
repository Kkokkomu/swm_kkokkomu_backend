package com.kkokkomu.short_news.news.dto.newsReaction.response;

import lombok.Builder;

@Builder
public record ReactionCntDto(
        Long like,
        Long surprise,
        Long sad,
        Long angry
) {
}
