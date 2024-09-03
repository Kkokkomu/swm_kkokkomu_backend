package com.kkokkomu.short_news.report.dto.reportedComment.request;

import com.kkokkomu.short_news.core.type.ECommentReport;
import jakarta.validation.constraints.NotNull;

public record CreateReportedCommentDto(
        @NotNull ECommentReport reason,
        @NotNull Long commentId
) {
}
