package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.constant.Constant;
import com.kkokkomu.short_news.dto.auth.request.SocialRegisterRequestDto;
import com.kkokkomu.short_news.dto.auth.response.AccessTokenDto;
import com.kkokkomu.short_news.dto.auth.response.JwtTokenDto;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.service.AuthService;
import com.kkokkomu.short_news.type.ELoginProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "로그인/회원가입")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "소셜 로그인 부가정보 입력")
    @PostMapping("/register")
    public ResponseDto<JwtTokenDto> socialRegister(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken,
                                                   @RequestBody @Valid SocialRegisterRequestDto socialRegisterRequestDto) {
        log.info("socialRegisterRequestDto : " + socialRegisterRequestDto);
        return ResponseDto.created(authService.socialRegister(accessToken, socialRegisterRequestDto));
    }

    @Operation(summary = "소셜 로그인 or 회원가입", description = "회원가입 필요시 access token만 반환, 로그인 완료시 access, refresh 둘 다 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 완료, access token과 refresh token 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtTokenDto.class))),
            @ApiResponse(responseCode = "201", description = "회원가입 필요, access token만 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessTokenDto.class))),
    })
    @PostMapping("/login/{provider}")
    public ResponseDto<?> authSocialLogin(@PathVariable String provider,
                                          @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
        log.info("accessToken : " + accessToken);
        return ResponseDto.ok(authService.authSocialLogin(accessToken, provider));
    }
}
