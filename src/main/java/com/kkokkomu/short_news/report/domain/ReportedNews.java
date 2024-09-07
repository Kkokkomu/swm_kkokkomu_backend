package com.kkokkomu.short_news.report.domain;

import com.kkokkomu.short_news.core.type.ENewsProgress;
import com.kkokkomu.short_news.core.type.ENewsReport;
import com.kkokkomu.short_news.core.type.ECommentProgress;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reported_news")
public class ReportedNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // Foreign key to User entity (신고한 유저)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    private News news; // Foreign key to News entity (신고된 뉴스)

    @Column(name = "reason", nullable = false)
    private ENewsReport reason; // 신고 이유

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt; // 신고 일시

    @Column(name = "progress", nullable = false)
    private ENewsProgress progress; // 처리 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent; // 담당자

    @Column(name = "executed_at")
    private LocalDateTime executedAt; // 처리 일시

    @Builder
    public ReportedNews(User reporter, News news, ENewsReport reason) {
        this.reporter = reporter;
        this.news = news;
        this.reason = reason;
        this.progress = ENewsProgress.UNEXECUTED;
        this.reportedAt = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 설정
    }

    public void execute(User admin) {
        this.reporter = admin;
        this.reportedAt = LocalDateTime.now();
        this.progress = ENewsProgress.EXECUTED;
    }

    public void dismiss(User admin) {
        this.reporter = admin;
        this.reportedAt = LocalDateTime.now();
        this.progress = ENewsProgress.DISMISSED;
    }

    public void updateNewsNull() {
        this.news = null;
    }
}
