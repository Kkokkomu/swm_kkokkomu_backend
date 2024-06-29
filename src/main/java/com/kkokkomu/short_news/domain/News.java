package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shortform_url", nullable = true)
    private String shortformUrl;

    @Column(name = "youtube_url", nullable = false)
    private String youtubeUrl;

    @Column(name = "instagram_url", nullable = false)
    private String instagramUrl;

    @Column(name = "related_url", nullable = false)
    private String relatedUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public News(String shortformUrl, String youtubeUrl, String instagramUrl, String relatedUrl) {
        this.shortformUrl = shortformUrl;
        this.youtubeUrl = youtubeUrl;
        this.instagramUrl = instagramUrl;
        this.relatedUrl = relatedUrl;
        this.createdAt = LocalDateTime.now();
    }

    public void updateShrotFormUrl(String shortformUrl, String youtubeUrl) {
        this.shortformUrl = shortformUrl;
        this.youtubeUrl = youtubeUrl;
    }
}
