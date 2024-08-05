package com.kkokkomu.short_news.dto.news.response;

import com.kkokkomu.short_news.dto.newsReaction.response.NewReactionByUserDto;
import com.kkokkomu.short_news.dto.newsReaction.response.ReactionCntDto;
import lombok.Builder;

import java.util.List;

@Builder
public record NewsListDto(
        NewsSummaryDto shortformList,
        ReactionCntDto reactionCnt,
        NewReactionByUserDto userReaction
) {
}
