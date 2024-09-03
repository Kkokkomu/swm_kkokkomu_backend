package com.kkokkomu.short_news.report.dto.commentReport.response;

import com.kkokkomu.short_news.report.domain.ReportedComment;
import lombok.Builder;

@Builder
public record CommentReportDto(
        Long reportId,
        String reason,
        Long reporterId,
        String createdAt
) {
    public static CommentReportDto of(ReportedComment reportedComment) {
        return CommentReportDto.builder()
                .reportId(reportedComment.getId())
                .reporterId(reportedComment.getReporter().getId())
                .reason(reportedComment.getReason().toString())
                .createdAt(reportedComment.getReportedAt().toString())
                .build();
    }
}
