package com.kkokkomu.short_news.news.dto.news.request;

import com.kkokkomu.short_news.core.type.ECategory;
import jakarta.validation.constraints.NotNull;

public record UpdateNewsDto(
        @NotNull Long id,
        String title,
        String summary,
        ECategory category,
        String shortformUrl,
        String youtubeUrl,
        String instagramUrl,
        String relatedUrl,
        String thumbnail
) {
}
