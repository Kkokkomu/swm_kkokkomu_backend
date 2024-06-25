package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @GetMapping("")
    public ResponseDto<?> helloController(){
        return ResponseDto.ok("hello, world!");
    }

    @GetMapping("/error")
    public String errorController(){
        throw new CommonException(ErrorCode.INVALID_PARAMETER);
    }
}
