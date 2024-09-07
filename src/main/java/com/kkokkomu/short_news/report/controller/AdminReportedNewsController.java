package com.kkokkomu.short_news.report.controller;

import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.report.dto.reportedComment.response.AdminCommentListDto;
import com.kkokkomu.short_news.report.dto.reportedNews.response.AdminReportedNewsDto;
import com.kkokkomu.short_news.report.service.ReportedNewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseDto<CursorResponseDto<List<AdminReportedNewsDto>>> readReportedNews(@RequestParam int size,
                                                                                          @RequestParam(required = false) Long cursorId
    ) {
        log.info("readReportedNews controller");
        return ResponseDto.ok(reportedNewsService.findAdminReportedNews(cursorId, size));
    }
}
