package com.kkokkomu.short_news.alarm.service;

import com.kkokkomu.short_news.alarm.domain.AlarmLog;
import com.kkokkomu.short_news.alarm.domain.FCMToken;
import com.kkokkomu.short_news.alarm.dto.fcm.request.CreateAlarmLogDto;
import com.kkokkomu.short_news.alarm.repository.AlarmLogRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmLogService {
    private final AlarmLogRepository alarmLogRepository;

    private final UserLookupService userLookupService;

    public Long getAlarmBadge(User user) {
        return alarmLogRepository.countByReceiverAndIsReadFalse(user);
    }

    public void createAlarmLog(CreateAlarmLogDto alarmLogDto) {
        alarmLogRepository.save(
                AlarmLog.builder()
                    .alarmType(alarmLogDto.alarmType())
                    .reply(alarmLogDto.comment())
                    .notification(alarmLogDto.notification())
                    .receiver(alarmLogDto.receiver())
                    .build()
        );
    }

    public void createAlarmLog(List<CreateAlarmLogDto> alarmLogDtos) {
        for (CreateAlarmLogDto alarmLogDto : alarmLogDtos) {
            createAlarmLog(alarmLogDto);
        }
    }
}
