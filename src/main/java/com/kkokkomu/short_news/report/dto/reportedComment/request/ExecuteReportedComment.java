package com.kkokkomu.short_news.report.dto.reportedComment.request;

import jakarta.validation.constraints.NotNull;

public record ExecuteReportedComment(
        @NotNull Long reportedCommentId
) {
}
