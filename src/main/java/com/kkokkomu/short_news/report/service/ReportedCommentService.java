package com.kkokkomu.short_news.report.service;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.comment.service.CommentLookupService;
import com.kkokkomu.short_news.report.domain.ReportedComment;
import com.kkokkomu.short_news.report.dto.commentReport.request.CreateCommentReportDto;
import com.kkokkomu.short_news.report.dto.commentReport.response.CommentReportDto;
import com.kkokkomu.short_news.report.repository.ReportedCommentRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportedCommentService {
    private final ReportedCommentRepository reportedCommentRepository;

    private final UserLookupService userLookupService;
    private final CommentLookupService commentLookupService;

    public CommentReportDto create(CreateCommentReportDto commentReportDto, Long userId) {
        // 신고자, 신고댓글, 댓글 작성자 유효성 확인
        User reporter = userLookupService.findUserById(userId);

        Comment comment = commentLookupService.findCommentById(commentReportDto.commentId());

        User writer = userLookupService.findUserById(comment.getUser().getId());

        // 생성
        ReportedComment reportedComment = reportedCommentRepository.save(
                ReportedComment.builder()
                        .comment(comment)
                        .reporter(reporter)
                        .reason(commentReportDto.reason())
                        .build()
        );

        // 사용자 신고 카운트 +1
        writer.updateReportedCnt();

        return CommentReportDto.of(reportedComment);
    }
}
