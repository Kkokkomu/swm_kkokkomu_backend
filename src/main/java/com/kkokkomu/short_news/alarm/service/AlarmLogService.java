package com.kkokkomu.short_news.alarm.service;

import com.kkokkomu.short_news.alarm.domain.AlarmLog;
import com.kkokkomu.short_news.alarm.domain.Notification;
import com.kkokkomu.short_news.alarm.dto.fcm.request.CreateAlarmLogDto;
import com.kkokkomu.short_news.alarm.repository.AlarmLogRepository;
import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.comment.service.CommentLookupService;
import com.kkokkomu.short_news.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmLogService {
    private final AlarmLogRepository alarmLogRepository;

    private final CommentLookupService commentLookupService;
    private final NotificationLookupService notificationLookupService;

    public Long getAlarmBadge(User user) {
        return alarmLogRepository.countByReceiverAndIsReadFalse(user);
    }

    public void createAlarmLog(CreateAlarmLogDto alarmLogDto) {
        Comment comment = commentLookupService.findCommentById(alarmLogDto.commentId());
        Notification notification = notificationLookupService.findNotificationById(alarmLogDto.notificationId());

        alarmLogRepository.save(
                AlarmLog.builder()
                    .alarmType(alarmLogDto.alarmType())
                    .reply(comment)
                    .notification(notification)
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
