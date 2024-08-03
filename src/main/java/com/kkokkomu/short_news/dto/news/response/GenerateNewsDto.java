package com.kkokkomu.short_news.dto.news.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GenerateNewsDto(
        NewsDto newsDto,
        String relatedUrl,
        List<String> keywords
) {
}
