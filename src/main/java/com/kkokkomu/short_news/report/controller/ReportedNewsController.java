package com.kkokkomu.short_news.report.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.report.dto.reportedComment.response.ReportedCommentDto;
import com.kkokkomu.short_news.report.dto.reportedNews.request.CreatedReportedNewsDto;
import com.kkokkomu.short_news.report.dto.reportedNews.response.ReportedNewsDto;
import com.kkokkomu.short_news.report.service.ReportedNewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "숏폼 신고")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/report/news")
public class ReportedNewsController {
    private final ReportedNewsService reportedNewsService;

    @Operation(summary = "숏폼 신고")
    @PostMapping("")
    public ResponseDto<ReportedNewsDto> createNewsReport(@Parameter(hidden = true) @UserId Long userId,
                                                         @RequestBody @Valid CreatedReportedNewsDto createdReportedNewsDto
    ) {
        log.info("createNewsReport controller");
        return ResponseDto.ok(reportedNewsService.create(createdReportedNewsDto, userId));
    }
}
