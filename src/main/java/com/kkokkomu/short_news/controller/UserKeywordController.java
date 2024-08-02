package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.annotation.UserId;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.userKeyword.request.CreateUserKeywordDto;
import com.kkokkomu.short_news.dto.userKeyword.request.RegisterUserKeyword;
import com.kkokkomu.short_news.dto.userKeyword.response.UserKeywordDto;
import com.kkokkomu.short_news.service.UserKeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 키워드")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user-keyword")
public class UserKeywordController {
    private final UserKeywordService userKeywordService;

    @Operation(summary = "유저 키워드 등록 (새 키워드 등록)")
    @PostMapping("/new")
    public ResponseDto<UserKeywordDto> createUserKeyword(@UserId Long userId, @RequestBody CreateUserKeywordDto createUserKeywordDto) {
        log.info("create keyword : {}", createUserKeywordDto);
        return ResponseDto.ok(userKeywordService.createUserKeyword(userId, createUserKeywordDto));
    }

    @Operation(summary = "유저 키워드 등록 (기존 키워드에 등록)")
    @PostMapping("")
    public ResponseDto<UserKeywordDto> registerUserKeyword(@UserId Long userId, @RequestBody RegisterUserKeyword registerUserKeyword) {
        log.info("register keyword : {}", registerUserKeyword);
        return ResponseDto.ok(userKeywordService.registerUserKeyword(userId, registerUserKeyword));
    }
}
