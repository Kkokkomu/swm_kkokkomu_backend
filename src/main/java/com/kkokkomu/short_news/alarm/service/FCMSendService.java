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
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private FCMSendService fcmSendService;

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
                            .notification(notification)
                            .alarmType(EAlarmType.NOTICE)
                            .build()
            );
        }

        return true;
    }

    // 대댓글 알림 전송
    public void sendReplyAlarm(Comment reply) {
        // 부모 댓글의 작성자를 알림 수신자로 설정
        User receiver = reply.getParent().getUser();

        // 유저 세팅이 맞지 않다면 전송안함
        if (!alarmSettingService.getReplySettingValid(receiver)) {
            return;
        }
        // 대댓 작성자가 댓글 작성자와 같으면 전송안함
        if (reply.getUser() == reply.getParent().getUser()) {
            log.info("writer is same as reply");
            return;
        }

        // 제목 및 본문 세팅
        String title = "새로운 대댓글 : " + reply.getUser().getNickname();
        String body = reply.getContent();

        // 알람 로그 저장
        alarmLogService.createAlarmLog(
                CreateAlarmLogDto.builder()
                        .alarmType(EAlarmType.REPLY)
                        .comment(reply)
                        .receiver(receiver)
                        .build()
        );

        // 부모 댓글 글쓴이에게 알람
        List<FCMToken> tokenList = fcmTokenRepository.findByUser(receiver);
        for (FCMToken token : tokenList) {
            PushAlarmDto pushAlarmDto = PushAlarmDto.builder()
                    .title(title)
                    .body(body)
                    .fcmToken(token.getToken())
                    .build();
            try{
                log.info("sendMessage 호출");
                fcmSendService.sendMessage(pushAlarmDto, receiver, EAndroidChannelId.REPLY);
            } catch (CommonException e) {
                log.info(e.getMessage());
            }
        }
        log.info("sendReplyAlarm 종료");
    }

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
