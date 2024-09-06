package com.kkokkomu.short_news.report.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.report.dto.reportedComment.request.CreateReportedCommentDto;
import com.kkokkomu.short_news.report.dto.reportedComment.response.AdminCommentListDto;
import com.kkokkomu.short_news.report.dto.reportedComment.response.ReportedCommentDto;
import com.kkokkomu.short_news.report.service.ReportedCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글 신고(관리자)")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/report/comment")
public class AdminReportedCommentController {
    private final ReportedCommentService reportedCommentService;


    @Operation(summary = "신고 리스트 조회")
    @GetMapping("")
    public ResponseDto<CursorResponseDto<List<AdminCommentListDto>>> readReportedComment(@RequestParam int size,
                                                                            @RequestParam(required = false) Long cursorId
    ) {
        log.info("readReportedComment controller");
        return ResponseDto.ok(reportedCommentService.findAllAdminComments(cursorId, size));
    }
}
