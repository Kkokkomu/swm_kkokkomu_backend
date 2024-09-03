package com.kkokkomu.short_news.report.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.core.type.EHomeFilter;
import com.kkokkomu.short_news.news.dto.news.response.NewsListDto;
import com.kkokkomu.short_news.report.service.ReportedCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

//    @Operation(summary = "댓글 신고")
//    @PostMapping("/list")
//    public ResponseDto<PagingResponseDto<List<NewsListDto>>> readNewsList(@Parameter(hidden = true) @UserId Long userId,
//                                                                          @Parameter(description = "politics, economy, social, entertain, sports, living, world, it를 , 로 구분", example = "social,world") @RequestParam String category,
//                                                                          @RequestParam EHomeFilter filter,
//                                                                          @RequestParam int page, @RequestParam int size) {
//        log.info("readNewsList controller");
//        return ResponseDto.ok(newsService.readNewsList(userId, category, filter, page, size));
//    }
}
