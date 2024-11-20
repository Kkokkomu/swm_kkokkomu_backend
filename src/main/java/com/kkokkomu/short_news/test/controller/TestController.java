package com.kkokkomu.short_news.test.controller;

import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.util.RSSUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "테스트")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {
    private final RSSUtil rssUtil;

    @Operation(summary = "테스트 hello world")
    @GetMapping("")
    public ResponseDto<String> helloController(){
        return ResponseDto.ok(rssUtil.getNewsisHotNewsUrl());
    }

    @Operation(summary = "테스트 error")
    @GetMapping("/error")
    public String errorController(){
        throw new CommonException(ErrorCode.INVALID_PARAMETER);
    }

    @GetMapping("/date")
    public String dateController(){
        return LocalDateTime.now().toString();
    }
}
