package com.kkokkomu.short_news.alarm.controller;

import com.kkokkomu.short_news.alarm.dto.request.CreateTokenDto;
import com.kkokkomu.short_news.alarm.dto.request.FcmSendDto;
import com.kkokkomu.short_news.alarm.dto.request.PushAlarmDto;
import com.kkokkomu.short_news.alarm.dto.response.FCMTokenDto;
import com.kkokkomu.short_news.alarm.service.FCMSendService;
import com.kkokkomu.short_news.alarm.service.FCMTokenService;
import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "알람")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class AlarmController {
    private final FCMSendService fcmSendService;
    private final FCMTokenService fcmTokenService;

    @Operation(summary = "알람 테스트")
    @GetMapping("/test")
    public ResponseDto<String> test(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestBody PushAlarmDto pushAlarmDto
            ) throws IOException {
        return ResponseDto.ok(fcmSendService.test(pushAlarmDto, userId));
    }

    @Operation(summary = "토큰 등록")
    @PostMapping("/token")
    public ResponseDto<FCMTokenDto> applyToken(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestBody CreateTokenDto createTokenDto
            ) {
        return ResponseDto.ok(fcmTokenService.verifyFCMToken(userId, createTokenDto.fcmToken()));
    }

    @Operation(summary = "토큰 삭제")
    @DeleteMapping("/token")
    public ResponseDto<String> deleteToken(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestParam(value = "fcmToken") String fcmToken
            ) {
        return ResponseDto.ok(fcmTokenService.deleteUserToken(fcmToken, userId));
    }


}
