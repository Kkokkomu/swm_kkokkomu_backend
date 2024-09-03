package com.kkokkomu.short_news.user.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.user.dto.user.response.MyPageDto;
import com.kkokkomu.short_news.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "마이페이지 유저 정보 조회")
    @GetMapping("/mypage")
    public ResponseDto<MyPageDto> readMyPageInfo(@Parameter(hidden = true) @UserId Long userId) {
        log.info("readMyPageInfo controller userId = {}", userId);
        return ResponseDto.ok(userService.readMyPageInfo(userId));
    }
}
