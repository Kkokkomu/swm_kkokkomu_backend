package com.kkokkomu.short_news.alarm.service;

import com.google.firebase.messaging.*;
import com.kkokkomu.short_news.alarm.domain.FCMToken;
import com.kkokkomu.short_news.alarm.dto.fcm.request.APNsConfiguration;
import com.kkokkomu.short_news.alarm.dto.fcm.request.AndroidConfiguration;
import com.kkokkomu.short_news.alarm.dto.fcm.request.CreateAlarmLogDto;
import com.kkokkomu.short_news.alarm.dto.fcm.request.PushAlarmDto;
import com.kkokkomu.short_news.alarm.repository.FCMTokenRepository;
import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.EAlarmType;
import com.kkokkomu.short_news.core.type.EAndroidChannelId;
import com.kkokkomu.short_news.core.util.TimeUtil;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.AlarmSettingService;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final FCMTokenService fcmTokenService;

    private final TimeUtil timeUtil;

    public String test(PushAlarmDto pushAlarmDto, Long userId) {
        User user = userLookupService.findUserById(userId);

        sendMessage(pushAlarmDto, user, EAndroidChannelId.GENERAL);

        return "success";
    }

    // 새 뉴스 알림 전손
    @Transactional
    public Boolean sendNewsAlarm(News news) {
        String title = "지금 확인하세요: 새로운 핫 이슈!";
        String body = news.getTitle();

        List<FCMToken> targetToken;
        if (timeUtil.isNight()) { // 밤이면 야간 알림이 동의되어있고 뉴스 알림 동의 토큰만 불러옴
            targetToken = fcmTokenRepository.findAllByNightYnTrueAndNewContentYnTrue();
        } else { // 밤이 아니면 뉴스 알림 동의 토큰들 불러옴
            targetToken = fcmTokenRepository.findAllByNewContentYnTrue();
        }
        for (FCMToken token : targetToken) {
            PushAlarmDto pushAlarmDto = PushAlarmDto.builder()
                    .title(title)
                    .body(body)
                    .fcmToken(token.getToken())
                    .build();
            try{
                sendMessage(pushAlarmDto, token.getUser(), EAndroidChannelId.NEWS_ARTICLE);
            } catch (CommonException e) {
                log.info(e.getMessage());
            }
        }
        return true;
    }

    // 공지 알림 전손
    @Transactional
    public Boolean sendNotification(com.kkokkomu.short_news.alarm.domain.Notification notification) {
        String title = "!! 새 공지사항 !!";
        String body = notification.getTitle();

        List<User> targetUser = userLookupService.findUserByInformYnTrue(); // 설정 유효한 유저들 가지고 오기
        List<CreateAlarmLogDto> createAlarmLogDtos = new ArrayList<>();
        for (User user : targetUser) {
            for (FCMToken fcmToken : user.getFcmTokens()) { // 유효한 유저들의 토큰을 타겟 토큰으로 설정
                PushAlarmDto pushAlarmDto = PushAlarmDto.builder()
                        .title(title)
                        .body(body)
                        .fcmToken(fcmToken.getToken())
                        .build();
                try{
                    sendMessage(pushAlarmDto, fcmToken.getUser(), EAndroidChannelId.NOTICE); // 전송
                } catch (CommonException e) {
                    log.info(e.getMessage());
                }
            }
            // 유저 당 로그는 하나씩
            createAlarmLogDtos.add(
                    CreateAlarmLogDto.builder()
                            .receiver(user)
                            .notificationId(notification.getId())
                            .alarmType(EAlarmType.NOTICE)
                            .build()
            );
        }



        return true;
    }

    // 대댓글 알림 전송
    @Async
    public void sendReplyAlarm(Long replyId, Long receiverId, String content) {
        log.info("Starting sendReplyAlarm with replyId: {}, receiverId: {}", replyId, receiverId);

        User receiver = userLookupService.findUserById(receiverId);
        log.info("Receiver loaded: {}", receiver != null ? receiver.getId() : "null");

        if (!alarmSettingService.getReplySettingValid(receiver)) {
            log.info("Receiver has disabled reply notifications.");
            return;
        }

        log.info("Fetching FCM tokens for user: {}", receiver.getId());
        List<FCMToken> tokenList = fcmTokenRepository.findByUser(receiver);
        log.info("Number of FCM tokens found: {}", tokenList.size());

        for (FCMToken token : tokenList) {
            log.info("Sending message to token: {}", token.getToken());
            PushAlarmDto pushAlarmDto = PushAlarmDto.builder()
                    .title("새로운 대댓글 : " + replyId)
                    .body(content)
                    .fcmToken(token.getToken())
                    .build();
            try {
                sendMessage(pushAlarmDto, receiver, EAndroidChannelId.REPLY);
            } catch (CommonException e) {
                log.info("FCM 전송 실패: " + e.getMessage());
            }
        }

        log.info("Creating alarm log for replyId: {}", replyId);
        alarmLogService.createAlarmLog(
                CreateAlarmLogDto.builder()
                        .alarmType(EAlarmType.REPLY)
                        .commentId(replyId)
                        .receiver(receiver)
                        .build()
        );
    }



    // 메세지 전송
    @Transactional
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
            fcmTokenService.deleteToken(pushAlarmDto.fcmToken());

            throw new CommonException(ErrorCode.INVALID_FCM_TOKEN);
        }
    }

}
