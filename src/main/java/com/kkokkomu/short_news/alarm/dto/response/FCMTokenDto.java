package com.kkokkomu.short_news.alarm.dto.response;

import com.kkokkomu.short_news.alarm.domain.FCMToken;
import lombok.Builder;

@Builder
public record FCMTokenDto(
        Long id,
        String token,
        String editedAt,
        String expiredAt
) {
    public static FCMTokenDto of(FCMToken fcmToken) {
        return FCMTokenDto.builder()
                .id(fcmToken.getId())
                .token(fcmToken.getToken())
                .editedAt(fcmToken.getEditedAt().toString())
                .expiredAt(fcmToken.getExpiredAt().toString())
                .build();
    }
}
