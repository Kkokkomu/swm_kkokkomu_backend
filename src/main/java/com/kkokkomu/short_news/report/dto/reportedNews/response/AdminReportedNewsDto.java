package com.kkokkomu.short_news.report.dto.reportedNews.response;

import com.kkokkomu.short_news.news.dto.news.response.NewsDto;
import com.kkokkomu.short_news.report.domain.ReportedNews;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record AdminReportedNewsDto(
        Long reportId,
        String reason,
        Long reporterId,
        String createdAt,
        String progress,
        NewsDto news
) {
    public static AdminReportedNewsDto of(ReportedNews reportedNews) {
        return AdminReportedNewsDto.builder()
                .reportId(reportedNews.getId())
                .reporterId(reportedNews.getReporter().getId())
                .reason(reportedNews.getReason().toString())
                .createdAt(reportedNews.getReportedAt().toString())
                .progress(reportedNews.getProgress().toString())
                .news(NewsDto.of(reportedNews.getNews()))
                .build();
    }

    public static List<AdminReportedNewsDto> of(List<ReportedNews> reportedNews) {
        List<AdminReportedNewsDto> adminReportedNewsDtos = new ArrayList<>();
        for (ReportedNews reportedNew : reportedNews) {
            adminReportedNewsDtos.add(AdminReportedNewsDto.of(reportedNew));
        }
        return adminReportedNewsDtos;
    }
}
