package com.kkokkomu.short_news.news.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.core.type.EHomeFilter;
import com.kkokkomu.short_news.news.dto.news.request.SharedCntDto;
import com.kkokkomu.short_news.news.dto.news.response.GuestNewsListDto;
import com.kkokkomu.short_news.news.dto.news.response.NewsDto;
import com.kkokkomu.short_news.news.dto.news.response.NewsInfoDto;
import com.kkokkomu.short_news.news.dto.news.response.NewsListDto;
import com.kkokkomu.short_news.news.service.HomeNewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "홈화면 뉴스")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/home/news")
public class HomeNewsController {
    private final HomeNewsService homeNewsService;

    @Operation(summary = "뉴스 리스트 조회")
    @GetMapping("/list")
    public ResponseDto<PagingResponseDto<List<NewsListDto>>> readNewsList(@Parameter(hidden = true) @UserId Long userId,
                                                                          @Parameter(description = "politics, economy, social, entertain, sports, living, world, it를 , 로 구분", example = "social,world") @RequestParam String category,
                                                                          @RequestParam EHomeFilter filter,
                                                                          @RequestParam int page, @RequestParam int size) {
        log.info("readNewsList controller");
        return ResponseDto.ok(homeNewsService.readNewsList(userId, category, filter, page, size));
    }

    @Operation(summary = "비로그인 뉴스 리스트 조회")
    @GetMapping("/list/guest")
    public ResponseDto<PagingResponseDto<List<GuestNewsListDto>>> guestReadNewsList(@RequestParam int page, @RequestParam int size) {
        log.info("guestReadNewsList controller");
        return ResponseDto.ok(homeNewsService.guestReadNewsList(page, size));
    }

    @Operation(summary = "뉴스 정보 조회")
    @GetMapping("/info")
    public ResponseDto<NewsInfoDto> readNewsInfo(@RequestParam Long newsId) {
        log.info("readNewsInfo controller");
        return ResponseDto.ok(homeNewsService.readNewsInfo(newsId));
    }

    @Operation(summary = "뉴스 공유 수 증가")
    @PostMapping("/shared")
    public ResponseDto<NewsDto> updateSharedCnt(@RequestBody SharedCntDto sharedCntDto) {
        log.info("updateSharedCnt controller");
        return ResponseDto.ok(homeNewsService.updateSharedCnt(sharedCntDto));
    }
}
