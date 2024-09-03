package com.kkokkomu.short_news.report.domain;

import com.kkokkomu.short_news.core.type.ENewsReport;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reported_shortform")
public class ReportedShortform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // Foreign key to User entity (신고한 유저)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news; // Foreign key to News entity (신고된 뉴스)

    @Column(name = "reason", nullable = false)
    private ENewsReport reason; // 신고 이유

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt; // 신고 일시

    @Builder
    public ReportedShortform(User reporter, News news, ENewsReport reason) {
        this.reporter = reporter;
        this.news = news;
        this.reason = reason;
        this.reportedAt = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 설정
    }
}
