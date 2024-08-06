package com.kkokkomu.short_news.dto.newsReaction.response;

import com.kkokkomu.short_news.domain.NewsReaction;
import lombok.Builder;

@Builder
public record NewsReactionDto(
        Long userId,
        Long newsId,
        String reaction
) {
    static public NewsReactionDto of(NewsReaction newsReaction) {
        return NewsReactionDto.builder()
                .userId(newsReaction.getUser().getId())
                .newsId(newsReaction.getNews().getId())
                .reaction(newsReaction.getReaction().toString())
                .build();
    }

}
