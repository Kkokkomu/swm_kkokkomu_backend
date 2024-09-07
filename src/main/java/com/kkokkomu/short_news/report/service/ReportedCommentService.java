package com.kkokkomu.short_news.report.service;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.comment.service.CommentLookupService;
import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.ECommentProgress;
import com.kkokkomu.short_news.report.domain.ReportedComment;
import com.kkokkomu.short_news.report.dto.reportedComment.request.CreateReportedCommentDto;
import com.kkokkomu.short_news.report.dto.reportedComment.request.ExecuteReportedComment;
import com.kkokkomu.short_news.report.dto.reportedComment.response.AdminCommentListDto;
import com.kkokkomu.short_news.report.dto.reportedComment.response.ReportedCommentDto;
import com.kkokkomu.short_news.report.repository.ReportedCommentRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportedCommentService {
    private final ReportedCommentRepository reportedCommentRepository;

    private final UserLookupService userLookupService;
    private final CommentLookupService commentLookupService;

    public ReportedCommentDto create(CreateReportedCommentDto commentReportDto, Long userId) {
        // 신고자, 신고댓글, 댓글 작성자 유효성 확인
        User reporter = userLookupService.findUserById(userId);

        Comment comment = commentLookupService.findCommentById(commentReportDto.commentId());

        // 이미 신고한 댓글인지 검사
        if (reportedCommentRepository.existsByCommentAndReporter(comment, reporter)) {
            throw new CommonException(ErrorCode.DUPLICATED_REPORTED_COMMENT);
        }

        // 생성
        ReportedComment reportedComment = reportedCommentRepository.save(
                ReportedComment.builder()
                        .comment(comment)
                        .reporter(reporter)
                        .reason(commentReportDto.reason())
                        .build()
        );

        return ReportedCommentDto.of(reportedComment);
    } // 댓글 신고

    /* 관리자 */

    @Transactional(readOnly = true)
    public CursorResponseDto<List<AdminCommentListDto>> findAllAdminComments(Long cursorId, int size) {

        // 커서에 해당하는 신고 내역이 존재하는지 검사
        if (cursorId != null && !reportedCommentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        // size에 따른 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(0, size);

        // 신고 리스트 조회
        Page<ReportedComment> results;
        if (cursorId == null) {
            // 처음 요청
            results = reportedCommentRepository.findFirstPageByProgressOrderByReportedAt(ECommentProgress.UNEXECUTED, pageRequest);
        } else {
            // 2번째부터
            results = reportedCommentRepository.findByProgressOrderByReportedAt(cursorId, ECommentProgress.UNEXECUTED, pageRequest);
        }
        List<ReportedComment> reportedComments = results.getContent();

        // 신고 관련 dto
        List<AdminCommentListDto> adminCommentListDtos = AdminCommentListDto.of(reportedComments);

        // 페이징 정보 dto
        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(adminCommentListDtos, cursorInfoDto);
    } // 신고 리스트 조회 (오래된순)

    @Transactional(readOnly = true)
    public CursorResponseDto<List<AdminCommentListDto>> findAllAdminExecutedComments(Long cursorId, int size) {

        // 커서에 해당하는 신고 내역이 존재하는지 검사
        if (cursorId != null && !reportedCommentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        // size에 따른 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(0, size);

        // 신고 리스트 조회
        Page<ReportedComment> results;
        if (cursorId == null) {
            // 처음 요청
            results = reportedCommentRepository.findFirstPageByProgressOrderByReportedAtDesc(ECommentProgress.EXECUTED, ECommentProgress.DISMISSED, pageRequest);
        } else {
            // 2번째부터
            results = reportedCommentRepository.findByProgressOrderByReportedAtDesc(cursorId, ECommentProgress.EXECUTED, ECommentProgress.DISMISSED, pageRequest);
        }
        List<ReportedComment> reportedComments = results.getContent();

        // 신고 관련 dto
        List<AdminCommentListDto> adminCommentListDtos = AdminCommentListDto.of(reportedComments);

        // 페이징 정보 dto
        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(adminCommentListDtos, cursorInfoDto);
    } // 신고 처리 완료 리스트 조회 (오래된순)

    @Transactional
    public ReportedCommentDto executeReport(ExecuteReportedComment executeReportedComment, Long adminId) {
        ReportedComment reportedComment = reportedCommentRepository.findById(executeReportedComment.reportedCommentId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REPORTED_COMMENT));

        if (!reportedComment.getProgress().equals(ECommentProgress.UNEXECUTED)) {
            throw new CommonException(ErrorCode.ALREADY_EXECUTED_COMMENT);
        }

        User adminUser = userLookupService.findUserById(adminId);

        User writer = reportedComment.getComment().getUser();

        // 작성자 제재
        writer.executeAboutComment();

        // 신고 내역 처리 완료
        reportedComment.execute(adminUser);

        // 댓글 삭제
        commentLookupService.deleteCommentById(reportedComment.getComment().getId());

        return ReportedCommentDto.of(reportedComment);
    } // 댓글 신고 처리

    public ReportedCommentDto dismissReport(ExecuteReportedComment executeReportedComment, Long adminId) {
        ReportedComment reportedComment = reportedCommentRepository.findById(executeReportedComment.reportedCommentId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REPORTED_COMMENT));

        if (!reportedComment.getProgress().equals(ECommentProgress.UNEXECUTED)) {
            throw new CommonException(ErrorCode.ALREADY_EXECUTED_COMMENT);
        }

        User adminUser = userLookupService.findUserById(adminId);

        // 신고 내역 기각 처리 완료
        reportedComment.dismiss(adminUser);

        return ReportedCommentDto.of(reportedComment);
    } // 댓글 신고 기각
}
