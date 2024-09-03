package com.kkokkomu.short_news.report.dto.reportedNews.request;

import com.kkokkomu.short_news.core.type.ENewsReport;
import jakarta.validation.constraints.NotNull;

public record CreatedReportedNewsDto(
        @NotNull ENewsReport reason,
        @NotNull Long newsId
) {
}
