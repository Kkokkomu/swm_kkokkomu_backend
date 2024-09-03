package com.kkokkomu.short_news.keyword.dto.keyword.response;

import lombok.Builder;

@Builder
public record SearchKeywordDto(
        Long keywordId,
        String keyword,
        Long usedCnt
) {
}
