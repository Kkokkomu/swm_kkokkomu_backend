package com.kkokkomu.short_news.news.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.news.dto.news.request.CreateGenerateNewsDto;
import com.kkokkomu.short_news.news.dto.news.response.*;
import com.kkokkomu.short_news.news.service.NewsService;
import com.kkokkomu.short_news.core.type.EHomeFilter;
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

    /************************** 홈화면 **************************/
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
    @GetMapping("/list/guest")
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

    /************************** 탐색 화면 **************************/

    @Operation(summary = "탐색 화면 카테고리 필터 조회")
    @GetMapping("/filter")
    public ResponseDto<CursorResponseDto<List<SearchNewsDto>>> readFilteredNews(@Parameter(description = "popular | politics | economy | social | entertain | sports | living | world | it") @RequestParam String category,
                                                                                @RequestParam(required = false) Long cursorId,
                                                                                @RequestParam int size) {
        log.info("readFilteredNews controller");

        if (category.equals("popular")) {
            return ResponseDto.ok(newsService.getPopularNewsFilteredByCategory(cursorId, size));
        } else {
            return ResponseDto.ok(newsService.getLatestNewsFilteredByCategory(category, cursorId, size));
        }
    }

    @Operation(summary = "뉴스 검색")
    @GetMapping("/search")
    public ResponseDto<CursorResponseDto<List<SearchNewsDto>>> searchNews(@Parameter(example = "popular,politics,economy,social,entertain,sports,living,world,it") @RequestParam String category,
                                                                            @RequestParam String text,
                                                                            @RequestParam EHomeFilter filter,
                                                                            @RequestParam(required = false) Long cursorId,
                                                                            @RequestParam int size,
                                                                            @UserId Long userId) {
        log.info("searchNews controller");

        if (filter == EHomeFilter.LATEST) {
            return ResponseDto.ok(newsService.searchLatestNews(category, text, cursorId, size));
        } else {
            return ResponseDto.ok(newsService.searchPopularNews(category, text, cursorId, size));
        }
    }

    @Operation(summary = "비로그인 뉴스 검색")
    @GetMapping("/search/guest")
    public ResponseDto<CursorResponseDto<List<SearchNewsDto>>> guestSearchNews(@Parameter(example = "popular,politics,economy,social,entertain,sports,living,world,it") @RequestParam String category,
                                                                                @RequestParam String text,
                                                                                @RequestParam EHomeFilter filter,
                                                                                @RequestParam(required = false) Long cursorId,
                                                                                @RequestParam int size) {
        log.info("guestSearchNews controller");

        if (filter == EHomeFilter.LATEST) {
            return ResponseDto.ok(newsService.searchLatestNews(category, text, cursorId, size));
        } else {
            return ResponseDto.ok(newsService.searchPopularNews(category, text, cursorId, size));
        }
    }

    /************************** 뉴스 생성 **************************/

    @PostMapping("/generate")
    public ResponseDto<List<GenerateNewsDto>> generateNews(@RequestBody CreateGenerateNewsDto createGenerateNewsDto) {
        log.info("generateNews controller");
        return ResponseDto.ok(newsService.generateNews(createGenerateNewsDto));
    }
}
