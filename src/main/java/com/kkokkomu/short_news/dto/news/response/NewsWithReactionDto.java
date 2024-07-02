package com.kkokkomu.short_news.dto.news.response;

import com.kkokkomu.short_news.dto.reaction.response.ReactionCntDto;
import com.kkokkomu.short_news.dto.reaction.response.ReactionWithUser;
import lombok.Builder;

@Builder
public record NewsWithReactionDto(
        NewsDtoWithId shortForm,
        ReactionCntDto reaction,
        ReactionWithUser reactionWithUser
) {
}
