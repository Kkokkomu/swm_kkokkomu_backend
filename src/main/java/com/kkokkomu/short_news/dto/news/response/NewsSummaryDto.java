package com.kkokkomu.short_news.dto.news.response;

import com.kkokkomu.short_news.domain.News;
import lombok.Builder;

@Builder
public record NewsSummaryDto(
        Long id,
        String shortformUrl,
        String relatedUrl
) {
    static public NewsSummaryDto of(News news) {
        return NewsSummaryDto.builder()
                .id(news.getId())
                .shortformUrl(news.getShortformUrl())
                .relatedUrl(news.getRelatedUrl())
                .build();
    }
}
