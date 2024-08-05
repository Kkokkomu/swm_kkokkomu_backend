package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reported_comment")
public class ReportedComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment; // Foreign key to Comment entity (신고된 댓글)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // Foreign key to User entity (제보자)

    @Column(name = "reason", nullable = false)
    private String reason; // 신고 사유

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt; // 신고 일시

    @Builder
    public ReportedComment(Comment comment, User reporter, String reason) {
        this.comment = comment;
        this.reporter = reporter;
        this.reason = reason;
        this.reportedAt = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 설정
    }
}
