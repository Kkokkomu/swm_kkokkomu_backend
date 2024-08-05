package com.kkokkomu.short_news.domain;

import com.kkokkomu.short_news.type.ENewsReaction;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news_reaction", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_news_id", columnList = "news_id"),
        @Index(name = "idx_reaction", columnList = "reaction")
})
public class NewsReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Foreign key to User entity (사용자 ID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news; // Foreign key to News entity (뉴스 ID)

    @Column(name = "reaction", nullable = false)
    @Enumerated(EnumType.STRING)
    private ENewsReaction reaction; // Foreign key to Reaction entity (감정표현 ID)

    @Builder
    public NewsReaction(User user, News news, ENewsReaction reaction) {
        this.user = user;
        this.news = news;
        this.reaction = reaction;
    }
}
