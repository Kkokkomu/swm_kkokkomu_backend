package com.kkokkomu.short_news.user.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.user.dto.user.request.UpdateAlarmSettingDto;
import com.kkokkomu.short_news.user.dto.user.response.UserDto;
import com.kkokkomu.short_news.user.service.AlarmSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알람 설정")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/alarmSetting")
public class AlarmSettingController {
    private final AlarmSettingService alarmSettingService;

    @Operation(summary = "알람 세팅")
    @PutMapping("")
    public ResponseDto<UserDto> alarmSetting(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestBody @Valid UpdateAlarmSettingDto updateAlarmSettingDto
    ) {
        log.info("alarmSetting controller userId = {}", userId);
        return ResponseDto.ok(alarmSettingService.updateAlarmSetting(updateAlarmSettingDto, userId));
    }
}
