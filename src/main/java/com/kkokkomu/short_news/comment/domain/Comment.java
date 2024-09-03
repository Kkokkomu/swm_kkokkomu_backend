package com.kkokkomu.short_news.comment.domain;

import com.kkokkomu.short_news.report.domain.ReportedComment;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_news_id", columnList = "news_id")
})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Foreign key to User entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news; // Foreign key to News entity

    @Column(name = "content", nullable = false)
    private String content; // 댓글 내용

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일자

    @Column(name = "edited_at")
    private LocalDateTime editedAt; // 수정 일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent; // 부모 댓글

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children; // 자식 댓글들

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportedComment> reportedComments;

    @Builder
    public Comment(User user, News news, String content, Comment parent) {
        this.user = user;
        this.news = news;
        this.content = content;
        this.parent = parent;
        this.createdAt = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 설정
        this.editedAt = LocalDateTime.now(); // 초기값을 현재 시간으로 설정
    }

    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now(); // 업데이트 시 변경 시간 갱신
    }

    public void update(String content) {
        this.content = content;
    }
}
