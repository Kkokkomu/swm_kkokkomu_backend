package com.kkokkomu.short_news.report.dto.reportedNews.response;

import com.kkokkomu.short_news.report.domain.ReportedNews;
import lombok.Builder;

@Builder
public record ReportedNewsDto(
        Long reportId,
        String reason,
        Long reporterId,
        String createdAt
) {
    public static ReportedNewsDto of(ReportedNews reportedNews) {
        return ReportedNewsDto.builder()
                .reportId(reportedNews.getId())
                .reporterId(reportedNews.getReporter().getId())
                .reason(reportedNews.getReason().toString())
                .createdAt(reportedNews.getReportedAt().toString())
                .build();
    }
}
