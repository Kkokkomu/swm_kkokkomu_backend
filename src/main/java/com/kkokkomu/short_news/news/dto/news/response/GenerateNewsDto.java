package com.kkokkomu.short_news.news.dto.news.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GenerateNewsDto(
        NewsDto newsDto,
        List<String> keywords
) {
}
