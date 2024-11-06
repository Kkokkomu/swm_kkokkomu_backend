package com.kkokkomu.short_news.alarm.service;

import com.kkokkomu.short_news.alarm.domain.Notification;
import com.kkokkomu.short_news.alarm.repository.NotificationRepository;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationLookupServiceImpl implements NotificationLookupService {
    private final NotificationRepository notificationRepository;

    @Override
    public Notification findNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NOTIFICATION));
    }
}
