package com.kkokkomu.short_news.dto.news.response;

import com.kkokkomu.short_news.domain.News;
import lombok.Builder;

@Builder
public record NewsDtoWithId(
        Long id,
        String shortformUrl,
        String youtubeUrl,
        String instagramUrl,
        String relatedUrl
) {
    static public NewsDtoWithId fromEntity(News news) {
        return NewsDtoWithId.builder()
                .id(news.getId())
                .shortformUrl(news.getShortformUrl())
                .instagramUrl(news.getInstagramUrl())
                .youtubeUrl(news.getYoutubeUrl())
                .relatedUrl(news.getRelatedUrl())
                .build();
    }
}
