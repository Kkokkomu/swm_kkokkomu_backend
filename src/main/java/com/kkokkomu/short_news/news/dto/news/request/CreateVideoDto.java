package com.kkokkomu.short_news.news.dto.news.request;

public record CreateVideoDto(
    String title,
    String summary,
    String category,
    String relatedUrl
) {
}
