package com.kkokkomu.short_news.domain;

import com.kkokkomu.short_news.event.NewsViewHistListener;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news_view_hist", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
@EntityListeners(NewsViewHistListener.class)
public class NewsViewHist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Foreign key to User entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news; // Foreign key to News entity

    @Column(name = "view_date", nullable = false)
    private LocalDateTime viewDate; // 시청 일자

    @Builder
    public NewsViewHist(User user, News news, LocalDateTime viewDate) {
        this.user = user;
        this.news = news;
        this.viewDate = viewDate;
    }
}
