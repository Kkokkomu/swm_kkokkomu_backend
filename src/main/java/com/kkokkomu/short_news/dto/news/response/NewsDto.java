package com.kkokkomu.short_news.dto.news.response;

import com.kkokkomu.short_news.domain.News;
import jakarta.persistence.Column;
import lombok.Builder;

@Builder
public record NewsDto(
        String shortformUrl,
        String youtubeUrl,
        String instagramUrl,
        String relatedUrl
) {
    static public NewsDto fromEntity(News news) {
        return NewsDto.builder()
                .shortformUrl(news.getShortformUrl())
                .instagramUrl(news.getInstagramUrl())
                .youtubeUrl(news.getYoutubeUrl())
                .relatedUrl(news.getRelatedUrl())
                .build();
    }
}
