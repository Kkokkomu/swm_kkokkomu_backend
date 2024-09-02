package com.kkokkomu.short_news.news.dto.news.response;

import com.kkokkomu.short_news.news.dto.newsReaction.response.NewReactionByUserDto;
import com.kkokkomu.short_news.news.dto.newsReaction.response.ReactionCntDto;
import lombok.Builder;

@Builder
public record NewsListDto(
        NewsSummaryDto shortformList,
        ReactionCntDto reactionCnt,
        NewReactionByUserDto userReaction
) {
}
