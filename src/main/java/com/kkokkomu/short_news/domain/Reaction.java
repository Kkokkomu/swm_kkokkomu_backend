package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reaction")
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Column(name = "like", nullable = false)
    private boolean like;

    @Column(name = "hate", nullable = false)
    private boolean hate;

    @Column(name = "expect", nullable = false)
    private boolean expect;

    @Column(name = "surprise", nullable = false)
    private boolean surprise;

    @Builder
    public Reaction(User user, News news, boolean like, boolean hate, boolean expect, boolean surprise) {
        this.user = user;
        this.news = news;
        this.like = like;
        this.hate = hate;
        this.expect = expect;
        this.surprise = surprise;
    }
}
