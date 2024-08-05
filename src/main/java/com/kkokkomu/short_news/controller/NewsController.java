package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.annotation.UserId;
import com.kkokkomu.short_news.dto.common.PagingResponseDto;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.news.response.GenerateNewsDto;
import com.kkokkomu.short_news.dto.news.response.GuestNewsListDto;
import com.kkokkomu.short_news.dto.news.response.NewsInfoDto;
import com.kkokkomu.short_news.dto.news.response.NewsListDto;
import com.kkokkomu.short_news.service.NewsService;
import com.kkokkomu.short_news.type.EHomeFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "뉴스")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;

    @Operation(summary = "뉴스 생성(이거 건들면안돼!!!!!!!!으어어ㅓ어)")
    @PostMapping("")
    public ResponseDto<GenerateNewsDto> generateNews() {
        log.info("generateNews controller");
        return ResponseDto.ok(newsService.generateNews());
    }

    @Operation(summary = "뉴스 리스트 조회")
    @GetMapping("/list")
    public ResponseDto<PagingResponseDto<List<NewsListDto>>> readNewsList(@Parameter(hidden = true) @UserId Long userId,
                                                                          @Parameter(description = "politics, economy, social, entertain, sports, living, world, it를 , 로 구분", example = "social,world") @RequestParam String category,
                                                                          @RequestParam EHomeFilter filter,
                                                                          @RequestParam int page, @RequestParam int size) {
        log.info("readNewsList controller");
        return ResponseDto.ok(newsService.readNewsList(userId, category, filter, page, size));
    }

    @Operation(summary = "비로그인 뉴스 리스트 조회")
    @GetMapping("/list")
    public ResponseDto<PagingResponseDto<List<GuestNewsListDto>>> guestReadNewsList(@RequestParam int page, @RequestParam int size) {
        log.info("guestReadNewsList controller");
        return ResponseDto.ok(newsService.guestReadNewsList(page, size));
    }

    @Operation(summary = "뉴스 정보 조회")
    @GetMapping("/info")
    public ResponseDto<NewsInfoDto> readNewsInfo(@RequestParam Long newsId) {
        log.info("readNewsInfo controller");
        return ResponseDto.ok(newsService.readNewsInfo(newsId));
    }
}
