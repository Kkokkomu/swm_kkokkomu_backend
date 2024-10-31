package com.kkokkomu.short_news.alarm.service;

import com.google.firebase.messaging.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.kkokkomu.short_news.alarm.domain.FCMToken;
import com.kkokkomu.short_news.alarm.dto.request.*;
import com.kkokkomu.short_news.alarm.repository.FCMTokenRepository;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.EAndroidChannelId;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
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

    public String test(PushAlarmDto pushAlarmDto, Long userId) {
        FCMToken fcmToken = fcmTokenRepository.findByToken(pushAlarmDto.fcmToken())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_FCMTOKEN));

        int badge = 0;

        log.info("token : " + fcmToken.getToken());
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(pushAlarmDto.title())
                        .setBody(pushAlarmDto.body())
                        .build())
                .setApnsConfig(apnsConfiguration.apnsConfig(badge))
                .setAndroidConfig(androidConfiguration.androidConfig(EAndroidChannelId.GENERAL))
                .setToken(fcmToken.getToken())
                .putData("testData", "testtest")
                .putData("type", "inform")
                .build();
        try {
            String result = firebaseMessaging.send(message);
            log.info("Successfully sent message: " + result);
            fcmToken.refreshToken(); // 토큰일자 갱신
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send message: " + e.getMessage());
        }

        return "success";
    }

//    /**
//     * 푸시 메시지 처리를 수행하는 비즈니스 로직
//     *
//     * @param fcmSendDto 모바일에서 전달받은 Object
//     * @return 성공(1), 실패(0)
//     */
//    public int sendMessageTo(FcmSendDto fcmSendDto) {
//        try {
//            String message = makeMessage(fcmSendDto);
//            RestTemplate restTemplate = new RestTemplate();
//            /**
//             * 추가된 사항 : RestTemplate 이용중 클라이언트의 한글 깨짐 증상에 대한 수정
//             * @refernece : https://stackoverflow.com/questions/29392422/how-can-i-tell-resttemplate-to-post-with-utf-8-encoding
//             */
//            restTemplate.getMessageConverters()
//                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Authorization", "Bearer " + getAccessToken());
//
//            HttpEntity entity = new HttpEntity<>(message, headers);
//
//            String API_URL = "<https://fcm.googleapis.com/v1/projects/adjh54-a0189/messages:send>";
//            ResponseEntity response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
//
//            log.info(String.valueOf(response.getStatusCode()));
//
//            return response.getStatusCode() == HttpStatus.OK ? 1 : 0;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }

//    /**
//     * Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰을 발급 받습니다.
//     *
//     * @return Bearer token
//     */
//    private String getAccessToken() throws IOException {
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
//                .createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));
//
//        googleCredentials.refreshIfExpired();
//        return googleCredentials.getAccessToken().getTokenValue();
//    }
//
//    /**
//     * FCM 전송 정보를 기반으로 메시지를 구성합니다. (Object -> String)
//     *
//     * @param fcmSendDto FcmSendDto
//     * @return String
//     */
//    private String makeMessage(FcmSendDto fcmSendDto) throws JsonProcessingException {
//
//        ObjectMapper om = new ObjectMapper();
//        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
//                .message(FcmMessageDto.Message.builder()
//                        .token(fcmSendDto.getToken())
//                        .notification(FcmMessageDto.Notification.builder()
//                                .title(fcmSendDto.getTitle())
//                                .body(fcmSendDto.getBody())
//                                .image(null)
//                                .build()
//                        ).build()).validateOnly(false).build();
//
//        return om.writeValueAsString(fcmMessageDto);
//    }
}
