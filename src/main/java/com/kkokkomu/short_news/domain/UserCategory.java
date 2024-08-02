package com.kkokkomu.short_news.domain;

import com.kkokkomu.short_news.type.ECategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_category", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class UserCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Foreign key to User entity (유저 ID)

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ECategory category; // 유저가 체크한 카테고리

    @Builder
    public UserCategory(User user, ECategory category) {
        this.user = user;
        this.category = category;
    }
}
