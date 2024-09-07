package com.kkokkomu.short_news.report.dto.reportedComment.response;

import com.kkokkomu.short_news.comment.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.report.domain.ReportedComment;
import com.kkokkomu.short_news.user.dto.user.response.CommentSummoryDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record AdminCommentListDto(
        Long id,
        CommentSummoryDto user,
        CommentDto comment,
        String reason,
        String reportedAt
) {
    public static AdminCommentListDto of(ReportedComment reportedComment) {
        return AdminCommentListDto.builder()
                .id(reportedComment.getId())
                .user(CommentSummoryDto.of(reportedComment.getComment().getUser()))
                .comment(reportedComment.getComment() != null ? CommentDto.of(reportedComment.getComment()) : null)
                .reason(reportedComment.getReason().toString())
                .reportedAt(reportedComment.getReportedAt().toString())
                .build();
    }

    public static List<AdminCommentListDto> of(List<ReportedComment> reportedComments) {
        List<AdminCommentListDto> adminCommentListDtos = new ArrayList<>();
        for (ReportedComment reportedComment : reportedComments) {
            adminCommentListDtos.add(AdminCommentListDto.of(reportedComment));
        }

        return adminCommentListDtos;
    }
}
