package com.kkokkomu.short_news.alarm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kkokkomu.short_news.alarm.dto.fcm.request.APNsConfiguration;
import com.kkokkomu.short_news.alarm.dto.fcm.request.AndroidConfiguration;
import com.kkokkomu.short_news.alarm.dto.fcm.request.PushAlarmDto;
import com.kkokkomu.short_news.alarm.repository.FCMTokenRepository;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.EAndroidChannelId;
import com.kkokkomu.short_news.core.util.TimeUtil;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.AlarmSettingService;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageSendService {
    private final APNsConfiguration apnsConfiguration;
    private final AndroidConfiguration androidConfiguration;
    private final FirebaseMessaging firebaseMessaging;

    private final AlarmLogService alarmLogService;
    private final FCMTokenService fcmTokenService;

    private final TimeUtil timeUtil;

    // 메세지 전송
    @Async
    public void sendMessage(PushAlarmDto pushAlarmDto, User user, EAndroidChannelId androidChannelId) {
        log.info("sendMessage");

        // 안읽은 알림 개수
        int badge = alarmLogService.getAlarmBadge(user).intValue();

        log.info("token : " + pushAlarmDto.fcmToken());
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(pushAlarmDto.title())
                        .setBody(pushAlarmDto.body())
                        .build())
                .setApnsConfig(apnsConfiguration.apnsConfig(badge))
                .setAndroidConfig(androidConfiguration.androidConfig(androidChannelId))
                .setToken(pushAlarmDto.fcmToken())
                .putData("testData", "testtest")
                .putData("type", "inform")
                .build();
        try {
            String result = firebaseMessaging.send(message);
            log.info("Successfully sent message: " + result);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send message: " + e.getMessage());
            fcmTokenService.deleteToken(pushAlarmDto.fcmToken());

            throw new CommonException(ErrorCode.INVALID_FCM_TOKEN);
        }
    }
}
