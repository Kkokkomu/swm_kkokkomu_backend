package com.kkokkomu.short_news.alarm.service;

import com.google.firebase.messaging.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.kkokkomu.short_news.alarm.domain.FCMToken;
import com.kkokkomu.short_news.alarm.dto.request.*;
import com.kkokkomu.short_news.alarm.repository.FCMTokenRepository;
import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.EAlarmType;
import com.kkokkomu.short_news.core.type.EAndroidChannelId;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.AlarmSettingService;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMSendService {
    @Value("${fcm.firebase-key}")
    private String firebaseConfigPath;

    private final APNsConfiguration apnsConfiguration;
    private final AndroidConfiguration androidConfiguration;
    private final FirebaseMessaging firebaseMessaging;

    private final FCMTokenRepository fcmTokenRepository;

    private final UserLookupService userLookupService;
    private final AlarmSettingService alarmSettingService;
    private final AlarmLogService alarmLogService;

    public String test(PushAlarmDto pushAlarmDto, Long userId) {
        User user = userLookupService.findUserById(userId);

        sendMessage(pushAlarmDto, user, EAndroidChannelId.GENERAL);

        return "success";
    }

    // 대댓글 알림 전송
    @Transactional
    public void sendReplyAlarm(Comment reply) {
        // 부모 댓글의 작성자를 알림 수신자로 설정
        User receiver = reply.getParent().getUser();

        // 유저 세팅이 맞지 않다면 전송안함
        if (!alarmSettingService.getReplySettingValid(receiver)) {
            return;
        }
        // 대댓 작성자가 댓글 작성자와 같으면 전송안함
        if (reply.getUser() == reply.getParent().getUser()) {
            return;
        }

        // 제목 및 본문 세팅
        String title = "새로운 대댓글 : " + reply.getUser().getNickname();
        String body = reply.getContent();

        // 부모 댓글 글쓴이에게 알람
        List<FCMToken> tokenList = fcmTokenRepository.findByUser(receiver);
        for (FCMToken token : tokenList) {
            PushAlarmDto pushAlarmDto = PushAlarmDto.builder()
                    .title(title)
                    .body(body)
                    .fcmToken(token.getToken())
                    .build();
            sendMessage(pushAlarmDto, receiver, EAndroidChannelId.REPLY);
        }

        // 알람 로그 저장
        alarmLogService.createAlarmLog(
                CreateAlarmLogDto.builder()
                        .alarmType(EAlarmType.REPLY)
                        .comment(reply)
                        .receiver(receiver)
                        .build()
        );
    }

    // 메세지 전송
    public void sendMessage(PushAlarmDto pushAlarmDto, User user, EAndroidChannelId androidChannelId) {
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
        }
    }

}
