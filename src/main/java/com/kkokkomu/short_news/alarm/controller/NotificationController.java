package com.kkokkomu.short_news.alarm.controller;

import com.kkokkomu.short_news.alarm.dto.fcm.request.PushAlarmDto;
import com.kkokkomu.short_news.alarm.dto.notification.request.CreateNotificationDto;
import com.kkokkomu.short_news.alarm.dto.notification.response.NotificationDto;
import com.kkokkomu.short_news.alarm.service.NotificationService;
import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "공지사항")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "공지사항 등록")
    @PostMapping("")
    public ResponseDto<NotificationDto> createNotificationController(@RequestBody CreateNotificationDto createNotificationDto) {
        log.info("createNotificationController");
        return ResponseDto.ok(notificationService.applyNotification(createNotificationDto));
    }

    @Operation(summary = "공지사항 조회")
    @GetMapping("")
    public ResponseDto<NotificationDto> getNotificationController(@RequestParam(value = "notificationId") Long notificationId) {
        log.info("createNotificationController");
        return ResponseDto.ok(notificationService.getNotification(notificationId));
    }
}
