package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.annotation.UserId;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.user.response.MyPageDto;
import com.kkokkomu.short_news.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseDto<MyPageDto> readMyPageInfo(@UserId Long userId) {
        log.info("readMyPageInfo controller userId = {}", userId);
        return ResponseDto.ok(userService.readMyPageInfo(userId));
    }
}
