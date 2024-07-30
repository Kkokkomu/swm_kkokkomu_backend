package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.constant.Constant;
import com.kkokkomu.short_news.dto.auth.request.SocialRegisterRequestDto;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseDto<?> socialRegister(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken,
                                         @RequestBody @Valid SocialRegisterRequestDto socialRegisterRequestDto) {
        log.info("socialRegisterRequestDto : " + socialRegisterRequestDto);
        return ResponseDto.created(authService.socialRegister(accessToken, socialRegisterRequestDto));
    }

    @PostMapping("/login/{provider}")
    public ResponseDto<?> authSocialLogin(@PathVariable String provider,
                                          @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
        log.info("accessToken : " + accessToken);
        return ResponseDto.ok(authService.authSocialLogin(accessToken, provider));
    }
}
