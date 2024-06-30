package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.service.NewsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/news")
public class NewsController {
    private final NewsService newsService;

    @PostMapping("")
    public ResponseDto<?> uploadShortNews(@RequestParam("file") MultipartFile file) {
        log.info("upload shortNews file");
        return ResponseDto.ok(newsService.uploadShortForm(file));
    }

    @GetMapping("")
    public ResponseDto<?> getShortNews(@RequestParam Long userId,
                                       @RequestParam int page,
                                       @RequestParam int size) {
        log.info("get shortNews page");
        return ResponseDto.ok(newsService.readShortForm(userId, page, size));
    }
}
