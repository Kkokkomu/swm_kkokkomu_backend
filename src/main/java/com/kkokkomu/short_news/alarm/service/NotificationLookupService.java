package com.kkokkomu.short_news.alarm.service;

import com.kkokkomu.short_news.alarm.domain.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationLookupService {
    Notification findNotificationById(Long id);

}
