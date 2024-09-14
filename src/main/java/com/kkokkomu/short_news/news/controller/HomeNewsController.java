package com.kkokkomu.short_news.news.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.core.type.EHomeFilter;
import com.kkokkomu.short_news.news.dto.news.request.SharedCntDto;
import com.kkokkomu.short_news.news.dto.news.response.GuestNewsInfoDto;
import com.kkokkomu.short_news.news.dto.news.response.NewsDto;
import com.kkokkomu.short_news.news.dto.news.response.NewsInfoDto;
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
    public ResponseDto<PagingResponseDto<List<NewsInfoDto>>> readNewsList(@Parameter(hidden = true) @UserId Long userId,
                                                                          @RequestParam(required = false) Long cursorId,
                                                                          @RequestParam EHomeFilter filter,
                                                                          @RequestParam int page, @RequestParam int size) {
        log.info("readNewsList controller");

        if (filter.equals(EHomeFilter.RECOMMEND)) {
            return ResponseDto.ok(homeNewsService.readNewsList(userId, cursorId, size));
        } else {
            return ResponseDto.ok(homeNewsService.readNewsList(userId, cursorId, size));
        }
    }

    @Operation(summary = "비로그인 뉴스 리스트 조회")
    @GetMapping("/list/guest")
    public ResponseDto<PagingResponseDto<List<GuestNewsInfoDto>>> guestReadNewsList(@RequestParam(required = false) Long cursorId,
                                                                                    @RequestParam int size) {
        log.info("guestReadNewsList controller");

        return ResponseDto.ok(homeNewsService.guestReadNewsList(cursorId, size));
    }



    @Operation(summary = "뉴스 공유수 증가")
    @PostMapping("/shared")
    public ResponseDto<NewsDto> updateSharedCnt(@RequestBody SharedCntDto sharedCntDto) {
        log.info("updateSharedCnt controller");
        return ResponseDto.ok(homeNewsService.updateSharedCnt(sharedCntDto));
    }

    @Operation(summary = "뉴스 관심 없음")
    @PostMapping("/not-interested")
    public ResponseDto<NewsDto> updateNotInterested(@RequestBody SharedCntDto sharedCntDto) {
        log.info("updateNotInterested controller");
        return ResponseDto.ok(homeNewsService.updateNotInterested(sharedCntDto));
    }

    @Operation(summary = "뉴스 조회수 증가")
    @PostMapping("/view")
    public ResponseDto<String> increaseViewCnt(@RequestBody SharedCntDto sharedCntDto,
                                               @Parameter(hidden = true) @UserId Long userId) {
        log.info("increaseViewCnt controller");
        return ResponseDto.ok(homeNewsService.increaseNewsView(sharedCntDto, userId));
    }
}
