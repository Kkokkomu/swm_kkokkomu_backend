package com.kkokkomu.short_news.report.dto.commentReport.request;

import com.kkokkomu.short_news.core.type.ECommentReport;
import jakarta.validation.constraints.NotNull;

public record CreateCommentReportDto(
        @NotNull ECommentReport reason,
        @NotNull Long commentId
) {
}
