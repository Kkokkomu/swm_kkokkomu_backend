package com.kkokkomu.short_news.news.dto.news.response;

import com.kkokkomu.short_news.news.domain.News;
import lombok.Builder;

import java.util.List;

@Builder
public record NewsWithKeywordDto(
    NewsDto news,
    List<String> keywords
) {
    public static NewsWithKeywordDto of(News news) {
        return NewsWithKeywordDto.builder()
                .news(NewsDto.of(news))
                .keywords(news.getNewsKeywords().stream()
                        .map(newsKeyword -> newsKeyword.getKeyword().getKeyword())
                        .toList())
                .build();
    }
}
