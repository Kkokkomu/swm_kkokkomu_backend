package com.kkokkomu.short_news.news.dto.news.response;

import com.kkokkomu.short_news.news.dto.newsReaction.response.ReactionCntDto;
import lombok.Builder;

@Builder
public record GuestNewsInfoDto(
        NewsWithKeywordDto info,
        ReactionCntDto reactionCnt
) {
}
