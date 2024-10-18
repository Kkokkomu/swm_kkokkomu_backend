package com.kkokkomu.short_news.news.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.news.dto.news.response.NewsInfoDto;
import com.kkokkomu.short_news.news.dto.news.response.SearchNewsDto;
import com.kkokkomu.short_news.news.dto.newsHist.response.CommentHistInfoDto;
import com.kkokkomu.short_news.news.dto.newsHist.response.NewsHistInfoDto;
import com.kkokkomu.short_news.news.service.NewsLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public ResponseDto<CursorResponseDto<List<CommentHistInfoDto>>> readCommentedNews(
            @UserId Long userId,
            @RequestParam(value = "cursorId", required = false) Long cursorId,
            @RequestParam("size") int size
    ) {
        log.info("readCommentedNews controller");
        return ResponseDto.ok(newsLogService.getNewsWithComment(userId, cursorId, size));
    }

    @Operation(summary = "감정표현한 뉴스 조회")
    @GetMapping("/reaction")
    public ResponseDto<CursorResponseDto<List<NewsInfoDto>>> readReactionNews(
            @UserId Long userId,
            @RequestParam(value = "cursorId", required = false) Long cursorId,
            @RequestParam("size") int size
    ) {
        log.info("readReactionNews controller");
        return ResponseDto.ok(newsLogService.getNewsWithReaction(userId, cursorId, size));
    }

    @Operation(summary = "시청한 뉴스 조회")
    @GetMapping("/view")
    public ResponseDto<CursorResponseDto<List<NewsHistInfoDto>>> readViewNews(
            @UserId Long userId,
            @RequestParam(value = "cursorId", required = false) Long cursorId,
            @RequestParam("size") int size
    ) {
        log.info("readViewNews controller");
        return ResponseDto.ok(newsLogService.getNewsWithHist(userId, cursorId, size));
    }

    @Operation(summary = "뉴스 시청기록 삭제")
    @DeleteMapping("/list")
    public ResponseDto<String> deleteViewNews(
            @Parameter(example = "1,4,7,10,15") @RequestParam(value = "newsIdList") String newsIdList,
            @Parameter(hidden = true) @UserId Long userId
    ) {
        log.info("deleteViewNews controller");
        return ResponseDto.ok(newsLogService.deleteNewsHist(userId, newsIdList));
    }

    @Operation(summary = "뉴스 시청기록 일괄삭제")
    @DeleteMapping("/user")
    public ResponseDto<String> deleteViewNewsByUser(
            @Parameter(hidden = true) @UserId Long userId
    ) {
        log.info("deleteViewNewsByUser controller");
        return ResponseDto.ok(newsLogService.deleteNewsHistByUserId(userId));
    }
}
