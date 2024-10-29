package com.kkokkomu.short_news.core.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kkokkomu.short_news.alarm.domain.FCMToken;
import com.kkokkomu.short_news.alarm.service.FCMTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FCMTokenScheduler {
    private final FCMTokenService fcmTokenService;

    // 만기 토큰 삭제 스케줄러
    @Scheduled(cron = "0 0 0 * * *")
    private void deleteFCMToken() throws FirebaseMessagingException {
        log.info("token manage scheduler start");

        fcmTokenService.deleteExpiredTokens(); // 만기 토큰 삭제
    }
}
