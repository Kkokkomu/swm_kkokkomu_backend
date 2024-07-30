package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_favorite_keyword", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class UserFavoriteKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Foreign key to User entity (사용자 ID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword; // Foreign key to Keyword entity (키워드 ID)

    @Builder
    public UserFavoriteKeyword(User user, Keyword keyword) {
        this.user = user;
        this.keyword = keyword;
    }
}
