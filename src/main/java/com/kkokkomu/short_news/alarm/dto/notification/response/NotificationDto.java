package com.kkokkomu.short_news.alarm.dto.notification.response;

import com.kkokkomu.short_news.alarm.domain.Notification;
import lombok.Builder;

@Builder
public record NotificationDto(
        Long id,
        String title,
        String body,
        String editedAt,
        String createdAt
) {
    static public NotificationDto of(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .editedAt(notification.getEditedAt().toString())
                .createdAt(notification.getCreatedAt().toString())
                .build();
    }
}
