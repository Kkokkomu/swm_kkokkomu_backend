package com.kkokkomu.short_news.alarm.dto.AlarmLog.response;

import com.kkokkomu.short_news.alarm.domain.AlarmLog;
import com.kkokkomu.short_news.alarm.dto.notification.response.NotificationDto;
import com.kkokkomu.short_news.comment.dto.comment.response.CommentDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record AlarmLogDto(
        Long alarmLogId,
        Boolean isRead,
        String createdAt,
        String alarmType,
        CommentDto reply,
        NotificationDto notification
) {
    public static AlarmLogDto of(AlarmLog alarmLog) {
        return AlarmLogDto.builder()
                .alarmLogId(alarmLog.getId())
                .isRead(alarmLog.getIsRead())
                .createdAt(alarmLog.getCreatedAt().toString())
                .alarmType(alarmLog.getAlarmType().toString())
                .reply(alarmLog.getReply() != null ? CommentDto.of(alarmLog.getReply()) : null)
                .notification(alarmLog.getNotification() != null ? NotificationDto.of(alarmLog.getNotification()) : null)
    }

    public static List<AlarmLogDto> of(List<AlarmLog> alarmLogs) {
        List<AlarmLogDto> alarmLogDtos = new ArrayList<>();
        for (AlarmLog alarmLog : alarmLogs) {
            alarmLogDtos.add(AlarmLogDto.of(alarmLog));
        }
        return alarmLogDtos;
    }
}
