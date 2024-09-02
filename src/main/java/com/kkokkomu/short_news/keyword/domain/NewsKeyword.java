package com.kkokkomu.short_news.keyword.domain;

import com.kkokkomu.short_news.news.domain.News;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news_keyword", indexes = {
        @Index(name = "idx_news_id", columnList = "news_id")
})
public class NewsKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news; // Foreign key to News entity (뉴스 ID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword; // Foreign key to Keyword entity (키워드 ID)

    @Builder
    public NewsKeyword(News news, Keyword keyword) {
        this.news = news;
        this.keyword = keyword;
    }
}
