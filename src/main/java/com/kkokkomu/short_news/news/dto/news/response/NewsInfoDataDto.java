package com.kkokkomu.short_news.news.dto.news.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record NewsInfoDataDto(
        String url,
        String title,
        NewsInfoSummaryDto summary,
        Map<String, String> keywords,
        String section
) {
}
