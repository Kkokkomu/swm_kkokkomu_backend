package com.kkokkomu.short_news.report.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.report.dto.reportedComment.response.AdminCommentListDto;
import com.kkokkomu.short_news.report.dto.reportedNews.request.ExecuteReportedNews;
import com.kkokkomu.short_news.report.dto.reportedNews.response.AdminReportedNewsDto;
import com.kkokkomu.short_news.report.service.ReportedNewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "뉴스 신고(관리자)")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/report/news")
public class AdminReportedNewsController {
    private final ReportedNewsService reportedNewsService;

    @Operation(summary = "관리자 뉴스 신고 리스트 조회")
    @GetMapping("/unexecuted")
    public ResponseDto<CursorResponseDto<List<AdminReportedNewsDto>>> readUnexecutedReportedNews(@RequestParam int size,
                                                                                          @RequestParam(required = false) Long cursorId
    ) {
        log.info("readUnexecutedReportedNews controller");
        return ResponseDto.ok(reportedNewsService.findUnexecutedAdminReportedNews(cursorId, size));
    }

    @Operation(summary = "관리자 뉴스 신고 처리완료 리스트 조회")
    @GetMapping("/executed")
    public ResponseDto<CursorResponseDto<List<AdminReportedNewsDto>>> readExecuedReportedNews(@RequestParam int size,
                                                                                       @RequestParam(required = false) Long cursorId
    ) {
        log.info("readExecuedReportedNews controller");
        return ResponseDto.ok(reportedNewsService.findExecutedAdminReportedNews(cursorId, size));
    }

    @Operation(summary = "관리자 뉴스 신고 처리")
    @PostMapping("/execute")
    public ResponseDto<AdminReportedNewsDto> executeReportedNews(@RequestBody ExecuteReportedNews executeReportedNews,
                                                                 @UserId Long adminId
    ) {
        log.info("executeReportedNews controller");
        return ResponseDto.ok(reportedNewsService.executeReportedNews(executeReportedNews, adminId));
    }
}
