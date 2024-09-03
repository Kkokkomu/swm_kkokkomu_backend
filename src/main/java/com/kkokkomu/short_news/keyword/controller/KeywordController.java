package com.kkokkomu.short_news.keyword.controller;

import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.keyword.dto.keyword.response.SearchKeywordDto;
import com.kkokkomu.short_news.keyword.service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "키워드")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/keyword")
public class KeywordController {
    private final KeywordService keywordService;

    @Operation(summary = "키워드 검색")
    @GetMapping("/list")
    public ResponseDto<PagingResponseDto<List<SearchKeywordDto>>> searchKeyword(@RequestParam String text,
                                                                                @RequestParam Integer page,
                                                                                @RequestParam Integer size) {
        log.info("searchKeyword {}", text);
        return ResponseDto.ok(keywordService.searchKeyword(text, page, size));
    }
}
