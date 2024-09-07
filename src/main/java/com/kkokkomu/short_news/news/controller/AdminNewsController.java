package com.kkokkomu.short_news.news.controller;

import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.news.dto.news.request.CreateGenerateNewsDto;
import com.kkokkomu.short_news.news.dto.news.request.UpdateNewsDto;
import com.kkokkomu.short_news.news.dto.news.response.GenerateNewsDto;
import com.kkokkomu.short_news.news.dto.news.response.NewsDto;
import com.kkokkomu.short_news.news.service.AdminNewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "관리자 뉴스")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/news")
public class AdminNewsController {
    private final AdminNewsService adminNewsService;

    @PostMapping("/generate")
    public ResponseDto<List<GenerateNewsDto>> generateNews(@RequestBody CreateGenerateNewsDto createGenerateNewsDto) {
        log.info("generateNews controller");
        return ResponseDto.ok(adminNewsService.generateNews(createGenerateNewsDto));
    }

    @Operation(summary = "뉴스 수정")
    @PostMapping("")
    public ResponseDto<NewsDto> updateNews(@RequestBody UpdateNewsDto updateNewsDto) {
        log.info("updateNews controller");
        return ResponseDto.ok(adminNewsService.updateNews(updateNewsDto));
    }
}
