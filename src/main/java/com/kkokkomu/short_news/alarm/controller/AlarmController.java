package com.kkokkomu.short_news.alarm.controller;

import com.kkokkomu.short_news.alarm.dto.request.FcmSendDto;
import com.kkokkomu.short_news.alarm.dto.request.PushAlarmDto;
import com.kkokkomu.short_news.alarm.service.FCMSendService;
import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "알람")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class AlarmController {
    private FCMSendService fcmSendService;

    @Operation(summary = "알람 테스트")
    @GetMapping("/test")
    public ResponseDto<String> test(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestBody PushAlarmDto pushAlarmDto
            ) throws IOException {
        return ResponseDto.ok(fcmSendService.test(pushAlarmDto, userId));
    }
}
