package com.kkokkomu.short_news.report.dto.reportedNews.request;

import jakarta.validation.constraints.NotNull;

public record ExecuteReportedNews(
        @NotNull Long reportedNewsId
) {
}
