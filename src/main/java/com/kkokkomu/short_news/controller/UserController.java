package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.user.request.LoginDto;
import com.kkokkomu.short_news.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseDto<?> login(@RequestBody LoginDto loginDto) {
        return ResponseDto.ok(userService.loginUser(loginDto));
    }
}
