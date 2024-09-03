package com.kkokkomu.short_news.report.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.core.type.EHomeFilter;
import com.kkokkomu.short_news.news.dto.news.response.NewsListDto;
import com.kkokkomu.short_news.report.dto.commentReport.request.CreateCommentReportDto;
import com.kkokkomu.short_news.report.dto.commentReport.response.CommentReportDto;
import com.kkokkomu.short_news.report.service.ReportedCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글 신고")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/report/comment")
public class ReportedCommentController {
    private final ReportedCommentService reportedCommentService;

    @Operation(summary = "댓글 신고")
    @PostMapping("")
    public ResponseDto<CommentReportDto> createCommentReport(@Parameter(hidden = true) @UserId Long userId,
                                                             @RequestBody @Valid CreateCommentReportDto createCommentReportDto
    ) {
        log.info("createCommentReport controller");
        return ResponseDto.ok(reportedCommentService.create(createCommentReportDto, userId));
    }
}
