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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("")
    public ResponseDto<NewsDto> updateNews(@RequestBody UpdateNewsDto updateNewsDto) {
        log.info("updateNews controller");
        return ResponseDto.ok(adminNewsService.updateNews(updateNewsDto));
    } // 뉴스 수정

    @DeleteMapping("")
    public ResponseDto<String> deleteNews(@RequestParam(value = "newsId") Long newsId) {
        log.info("updateNews controller");
        return ResponseDto.ok(adminNewsService.deleteNews(newsId));
    } // 뉴스 삭제

    @PutMapping("/rank")
    public String syncGlobalRank() {
        log.info("syncGlobalRank controller");
        adminNewsService.syncRanking();
        return "success";
    }
}
