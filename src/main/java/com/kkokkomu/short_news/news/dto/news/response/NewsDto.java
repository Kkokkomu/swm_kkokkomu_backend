package com.kkokkomu.short_news.news.dto.news.response;

import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.core.type.ECategory;
import lombok.Builder;

@Builder
public record NewsDto(
        String shortformUrl, // 숏폼 URL
        String youtubeUrl, // YouTube URL
        String instagramUrl, // Instagram URL
        String relatedUrl, // 관련 기사 URL
        String thumbnail, // 썸네일 이미지 링크
        int viewCnt, // 조회수
        String title,
        String summary, // 요약 스크립트
        int sharedCnt, // 공유수
        ECategory category, // 카테고리
        String createdAt, // 생성 일자
        String editedAt // 변경 일자
) {
    static public NewsDto of(News news) {
        return NewsDto.builder()
                .shortformUrl(news.getShortformUrl())
                .youtubeUrl(news.getYoutubeUrl())
                .instagramUrl(news.getInstagramUrl())
                .relatedUrl(news.getRelatedUrl())
                .thumbnail(news.getThumbnail())
                .viewCnt(news.getViewCnt())
                .title(news.getTitle())
                .summary(news.getSummary())
                .sharedCnt(news.getSharedCnt())
                .category(news.getCategory())
                .createdAt(news.getCreatedAt().toString())
                .editedAt(news.getEditedAt().toString())
                .build();
    }
}
