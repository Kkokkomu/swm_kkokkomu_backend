package com.kkokkomu.short_news.dto.news.response;

import com.kkokkomu.short_news.dto.reaction.response.ReactionCntDto;
import lombok.Builder;

public record NewsWithReactionDto(
        NewsDto shortForm,
        ReactionCntDto reaction
) {
    @Builder
    public NewsWithReactionDto(NewsDto shortForm, ReactionCntDto reaction) {
        this.shortForm = shortForm;
        this.reaction = reaction;
    }
}
