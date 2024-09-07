package com.kkokkomu.short_news.news.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.core.type.EHomeFilter;
import com.kkokkomu.short_news.news.dto.news.response.NewsListDto;
import com.kkokkomu.short_news.news.dto.news.response.SearchNewsDto;
import com.kkokkomu.short_news.news.service.NewsLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "뉴스 시청기록")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage/log")
public class NewsLogController {
    private final NewsLogService newsLogService;

    @Operation(summary = "댓글 달았던 뉴스 조회")
    @GetMapping("/commented")
    public ResponseDto<CursorResponseDto<List<SearchNewsDto>>> readCommentedNews(
            @UserId Long userId,
            @RequestParam(value = "cursorId", required = false) Long cursorId,
            @RequestParam("size") int size
    ) {
        log.info("readCommentedNews controller");
        return ResponseDto.ok(newsLogService.searchNewsWithComment(userId, cursorId, size));
    }

    @Operation(summary = "감정표현한 뉴스 조회")
    @GetMapping("/reaction")
    public ResponseDto<CursorResponseDto<List<SearchNewsDto>>> readReactionNews(
            @UserId Long userId,
            @RequestParam(value = "cursorId", required = false) Long cursorId,
            @RequestParam("size") int size
    ) {
        log.info("readReactionNews controller");
        return ResponseDto.ok(newsLogService.searchNewsWithReaction(userId, cursorId, size));
    }
}
