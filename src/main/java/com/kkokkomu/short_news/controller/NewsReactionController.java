package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.annotation.UserId;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.news.response.GenerateNewsDto;
import com.kkokkomu.short_news.dto.newsReaction.request.CreateNewsReactionDto;
import com.kkokkomu.short_news.dto.newsReaction.response.NewsReactionDto;
import com.kkokkomu.short_news.service.NewsReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "뉴스 감정표현")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/news-reaction")
public class NewsReactionController {
    private final NewsReactionService newsReactionService;

    @Operation(summary = "뉴스 감정표현 생성")
    @PostMapping("")
    public ResponseDto<NewsReactionDto> addNewsReaction(@UserId Long userId,
                                                        @RequestBody CreateNewsReactionDto createNewsReactionDto) {
        log.info("addNewsReaction controller");
        return ResponseDto.ok(newsReactionService.createNewsReaction(userId, createNewsReactionDto));
    }
}
