package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "related_news", indexes = {
        @Index(name = "idx_news_id", columnList = "news_id")
})
public class RelatedNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news; // Foreign key to News entity

    @Column(name = "related_url", nullable = false)
    private String relatedUrl; // 관련 URL

    @Builder
    public RelatedNews(News news, String relatedUrl) {
        this.news = news;
        this.relatedUrl = relatedUrl;
    }
}
