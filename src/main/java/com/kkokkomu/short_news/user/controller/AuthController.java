package com.kkokkomu.short_news.user.controller;

import com.kkokkomu.short_news.core.constant.Constant;
import com.kkokkomu.short_news.user.dto.auth.request.SocialRegisterRequestDto;
import com.kkokkomu.short_news.user.dto.auth.response.JwtTokenDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.user.service.AuthService;
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
                            schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "201", description = "회원가입 필요, access token만 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))),
    })
    @PostMapping("/login/{provider}")
    public ResponseDto<?> authSocialLogin(@PathVariable String provider,
                                          @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
        log.info("accessToken : " + accessToken);
        return ResponseDto.ok(authService.authSocialLogin(accessToken, provider));
    }

    @Operation(summary = "소셜 로그인 or 회원가입", description = "회원가입 필요시 access token만 반환, 로그인 완료시 access, refresh 둘 다 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 완료, access token과 refresh token 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "201", description = "회원가입 필요, access token만 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))),
    })
    @PostMapping("/login/admin/{provider}")
    public ResponseDto<?> adminSocialLogin(@PathVariable String provider,
                                          @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
        log.info("accessToken : " + accessToken);
        return ResponseDto.ok(authService.authSocialLogin(accessToken, provider));
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseDto<JwtTokenDto> refresh(
            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String refreshToken){
        return ResponseDto.ok(authService.refresh(refreshToken));
    }

    // swagger 표기용
    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseDto<String> logout() {

        return ResponseDto.ok("");
    }
}
