package com.kkokkomu.short_news.report.dto.reportedComment.response;

import com.kkokkomu.short_news.report.domain.ReportedComment;
import lombok.Builder;

@Builder
public record ReportedCommentDto(
        Long reportId,
        String reason,
        Long reporterId,
        String createdAt
) {
    public static ReportedCommentDto of(ReportedComment reportedComment) {
        return ReportedCommentDto.builder()
                .reportId(reportedComment.getId())
                .reporterId(reportedComment.getReporter().getId())
                .reason(reportedComment.getReason().toString())
                .createdAt(reportedComment.getReportedAt().toString())
                .build();
    }
}
