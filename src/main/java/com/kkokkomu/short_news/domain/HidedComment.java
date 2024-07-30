package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hided_comment", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class HidedComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Foreign key to User entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment; // Foreign key to Comment entity

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 처리 일시

    @Builder
    public HidedComment(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
        this.createdAt = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 설정
    }
}
