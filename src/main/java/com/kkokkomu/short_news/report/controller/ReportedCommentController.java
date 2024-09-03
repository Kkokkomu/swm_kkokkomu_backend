package com.kkokkomu.short_news.report.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.report.dto.reportedComment.request.CreateReportedCommentDto;
import com.kkokkomu.short_news.report.dto.reportedComment.response.ReportedCommentDto;
import com.kkokkomu.short_news.report.service.ReportedCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 신고")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/report/comment")
public class ReportedCommentController {
    private final ReportedCommentService reportedCommentService;

    @Operation(summary = "댓글 신고")
    @PostMapping("")
    public ResponseDto<ReportedCommentDto> createCommentReport(@Parameter(hidden = true) @UserId Long userId,
                                                               @RequestBody @Valid CreateReportedCommentDto createCommentReportDto
    ) {
        log.info("createCommentReport controller");
        return ResponseDto.ok(reportedCommentService.create(createCommentReportDto, userId));
    }
}
