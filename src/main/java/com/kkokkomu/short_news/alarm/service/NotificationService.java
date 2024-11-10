package com.kkokkomu.short_news.alarm.service;

import com.kkokkomu.short_news.alarm.domain.Notification;
import com.kkokkomu.short_news.alarm.dto.notification.request.CreateNotificationDto;
import com.kkokkomu.short_news.alarm.dto.notification.response.NotificationDto;
import com.kkokkomu.short_news.alarm.repository.NotificationRepository;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;

    private final FCMSendService fcmSendService;

    public NotificationDto applyNotification(CreateNotificationDto notificationDto) {
        Notification notification = notificationRepository.save(
                Notification.builder()
                        .title(notificationDto.title())
                        .body(notificationDto.body())
                        .build()
        );

        fcmSendService.sendNotification(notification);

        return NotificationDto.of(notification);
    }

    public NotificationDto getNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NOTIFICATION));

        return NotificationDto.of(notification);
    }
}
