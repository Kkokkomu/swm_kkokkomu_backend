package com.kkokkomu.short_news.news.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.core.type.EHomeFilter;
import com.kkokkomu.short_news.news.dto.news.response.GuestNewsInfoDto;
import com.kkokkomu.short_news.news.dto.news.response.NewsInfoDto;
import com.kkokkomu.short_news.news.dto.news.response.NewsWithKeywordDto;
import com.kkokkomu.short_news.news.dto.news.response.SearchNewsDto;
import com.kkokkomu.short_news.news.service.SearchNewsService;
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

@Tag(name = "탐색화면 뉴스")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/search/news")
public class SearchNewsController {
    private final SearchNewsService searchNewsService;

    @Operation(summary = "탐색 화면 카테고리 필터 조회")
    @GetMapping("/filter")
    public ResponseDto<CursorResponseDto<List<SearchNewsDto>>> readFilteredNews(@Parameter(description = "popular | politics | economy | social | entertain | sports | living | world | it") @RequestParam String category,
                                                                                @RequestParam(required = false) Long cursorId,
                                                                                @RequestParam int size) {
        log.info("readFilteredNews controller");

        if (category.equals("popular")) {
            return ResponseDto.ok(searchNewsService.getPopularNewsFilteredByCategory(cursorId, size));
        } else {
            return ResponseDto.ok(searchNewsService.getLatestNewsFilteredByCategory(category, cursorId, size));
        }
    }

    @Operation(summary = "뉴스 검색")
    @GetMapping("")
    public ResponseDto<CursorResponseDto<List<SearchNewsDto>>> searchNews(@Parameter(example = "politics,economy,social,entertain,sports,living,world,it") @RequestParam String category,
                                                                          @RequestParam String text,
                                                                          @RequestParam EHomeFilter filter,
                                                                          @RequestParam(required = false) Long cursorId,
                                                                          @RequestParam int size) {
        log.info("searchNews controller");

        if (filter == EHomeFilter.LATEST) {
            return ResponseDto.ok(searchNewsService.searchLatestNews(category, text, cursorId, size));
        } else {
            return ResponseDto.ok(searchNewsService.searchPopularNews(category, text, cursorId, size));
        }
    }

    @Operation(summary = "비로그인 뉴스 검색")
    @GetMapping("/guest")
    public ResponseDto<CursorResponseDto<List<SearchNewsDto>>> guestSearchNews(@Parameter(example = "popular,politics,economy,social,entertain,sports,living,world,it") @RequestParam String category,
                                                                               @RequestParam String text,
                                                                               @RequestParam EHomeFilter filter,
                                                                               @RequestParam(required = false) Long cursorId,
                                                                               @RequestParam int size) {
        log.info("guestSearchNews controller");

        if (filter == EHomeFilter.LATEST) {
            return ResponseDto.ok(searchNewsService.searchLatestNews(category, text, cursorId, size));
        } else {
            return ResponseDto.ok(searchNewsService.searchPopularNews(category, text, cursorId, size));
        }
    }

    @Operation(summary = "뉴스 정보 조회")
    @GetMapping("/info")
    public ResponseDto<NewsInfoDto> readNewsInfo(@UserId Long userId,
                                                 @RequestParam Long newsId) {
        log.info("readNewsInfo controller");
        return ResponseDto.ok(searchNewsService.readNewsInfo(userId, newsId));
    }

    @Operation(summary = "비로그인 뉴스 정보 조회")
    @GetMapping("/info/guest")
    public ResponseDto<GuestNewsInfoDto> GuestReadNewsInfo(@RequestParam Long newsId) {
        log.info("GuestReadNewsInfo controller");
        return ResponseDto.ok(searchNewsService.guestReadNewsInfo(newsId));
    }
}
