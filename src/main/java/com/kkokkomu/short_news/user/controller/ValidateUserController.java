package com.kkokkomu.short_news.user.controller;

import com.kkokkomu.short_news.user.dto.validateUser.EmailValicate;
import com.kkokkomu.short_news.user.service.ValidateUserService;
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
    private final ValidateUserService validateUserService;

    /****** 안드로이드 url 회원탈퇴 ******/
    @PostMapping("")
    public String sendValidateEmail(@RequestBody EmailValicate emailValicate) {
        log.info("sendValidateEmail controller {}", emailValicate.email());
        return validateUserService.sendValidateCodeByEmail(emailValicate);
    }

    @GetMapping("/{authcode}")
    public String validateUser(@PathVariable String authcode) {
        log.info("validateUser controller");
        return validateUserService.validateUser(authcode);
    }
}
