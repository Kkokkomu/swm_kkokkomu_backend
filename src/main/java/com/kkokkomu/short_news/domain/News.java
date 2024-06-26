package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shortform_url", nullable = false)
    private String shortformUrl;

    @Column(name = "youtube_url", nullable = false)
    private String youtubeUrl;

    @Column(name = "instagram_url", nullable = false)
    private String instagramUrl;

    @Column(name = "related_url", nullable = false)
    private String relatedUrl;

    @Builder
    public News(String shortformUrl, String youtubeUrl, String instagramUrl, String relatedUrl) {
        this.shortformUrl = shortformUrl;
        this.youtubeUrl = youtubeUrl;
        this.instagramUrl = instagramUrl;
        this.relatedUrl = relatedUrl;
    }
}
