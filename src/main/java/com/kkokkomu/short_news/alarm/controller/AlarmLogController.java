package com.kkokkomu.short_news.alarm.controller;

import com.kkokkomu.short_news.alarm.dto.AlarmLog.require.UpdateAlarmLogDto;
import com.kkokkomu.short_news.alarm.dto.AlarmLog.response.AlarmLogDto;
import com.kkokkomu.short_news.alarm.dto.fcm.request.CreateTokenDto;
import com.kkokkomu.short_news.alarm.dto.fcm.response.FCMTokenDto;
import com.kkokkomu.short_news.alarm.service.AlarmLogService;
import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "알람 로그")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/alarmLog")
public class AlarmLogController {
    private final AlarmLogService alarmLogService;

    @Operation(summary = "알람 로그 조회")
    @GetMapping("/list")
    public ResponseDto<CursorResponseDto<List<AlarmLogDto>>> getAlarmLogList(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestParam(value = "cursorId", required = false) Long cursorId,
            @RequestParam(value = "size") int size
    ) {
        log.info("getAlarmLogList");
        return ResponseDto.ok(alarmLogService.getAlarmLogList(userId, cursorId, size));
    }

    @Operation(summary = "알람 로그 읽음처리")
    @PutMapping("/isRead")
    public ResponseDto<List<AlarmLogDto>> updateLogIsRead(
            @RequestBody UpdateAlarmLogDto updateAlarmLogDto
            ) {
        return ResponseDto.ok(alarmLogService.updateAlarmLogRead(updateAlarmLogDto));
    }
}
