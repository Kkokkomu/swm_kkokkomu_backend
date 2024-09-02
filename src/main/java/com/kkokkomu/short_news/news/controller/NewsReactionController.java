package com.kkokkomu.short_news.news.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.news.dto.newsReaction.request.CreateNewsReactionDto;
import com.kkokkomu.short_news.news.dto.newsReaction.response.NewsReactionDto;
import com.kkokkomu.short_news.news.service.NewsReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "뉴스 감정표현")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/news-reaction")
public class NewsReactionController {
    private final NewsReactionService newsReactionService;

    @Operation(summary = "뉴스 감정표현 생성")
    @PostMapping("")
    public ResponseDto<NewsReactionDto> addNewsReaction(@Parameter(hidden = true) @UserId Long userId,
                                                        @RequestBody CreateNewsReactionDto createNewsReactionDto) {
        log.info("addNewsReaction controller");
        return ResponseDto.ok(newsReactionService.createNewsReaction(userId, createNewsReactionDto));
    }

    @Operation(summary = "뉴스 감정표현 삭제")
    @DeleteMapping("")
    public ResponseDto<String> removeNewsReaction(@Parameter(hidden = true) @UserId Long userId,
                                                  @RequestParam Long newsId,
                                                  @Parameter(description = "like | sad | surprise | angry") @RequestParam String reaction) {
        log.info("removeNewsReaction controller");
        return ResponseDto.ok(newsReactionService.deleteNewsReaction(userId, newsId, reaction));
    }
}
