package com.kkokkomu.short_news.user.controller;

import com.kkokkomu.short_news.user.dto.validateUser.EmailValicate;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저 인증")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateUserController {

    /****** 안드로이드 url 회원탈퇴 ******/
    @PostMapping("")
    public String validateUser(@RequestBody EmailValicate emailValicate) {
        log.info("validateUser controller {}", emailValicate.email());
        return emailValicate.email();
    }
}
