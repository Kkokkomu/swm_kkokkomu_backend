package com.kkokkomu.short_news.alarm.service;

import com.kkokkomu.short_news.alarm.domain.FCMToken;
import com.kkokkomu.short_news.alarm.dto.request.CreateTokenDto;
import com.kkokkomu.short_news.alarm.dto.response.FCMTokenDto;
import com.kkokkomu.short_news.alarm.repository.FCMTokenRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMTokenService {
    private final FCMTokenRepository fcmTokenRepository;

    private final UserLookupService userLookupService;

    /* FCM TOKEN 등록, 회원가입 시 사용하기 */
    /* 로그인 및 회원가입 시
    * */
    @Transactional
    public FCMTokenDto applyFCMToken(CreateTokenDto createTokenDto, Long userId) {
        log.info("Applying FCM token: {}", createTokenDto.fcmToken());

        // 같은 디바이스에 등록된 다른 토큰 전부 삭제
        List<FCMToken> duplicateDevices = fcmTokenRepository.findAllByDeviceId(createTokenDto.deviceId());
        deleteTokens(duplicateDevices);

        // FCM 토큰 저장
        FCMToken fcmToken = createFCMToken(userId, createTokenDto.deviceId(), createTokenDto.fcmToken());

        return FCMTokenDto.of(fcmToken);
    }

    // 토큰 update 필요 여부 검증 메서드, 앱진입, 로그인 시 사용
    public FCMTokenDto verifyFCMToken(Long userId, String deviceId, String fcmToken) {
        log.info("verify token : {}", fcmToken);

        // 토큰이 없거나 다르면 생성
        Optional<FCMToken> fcmTokenOptional = fcmTokenRepository.findByDeviceIdAndToken(deviceId, fcmToken);

        FCMToken token = null;
        if (fcmTokenOptional.isEmpty()) { // 토큰이 비어있으면 새로 생성
            token = createFCMToken(userId, deviceId, fcmToken);
        } else if(!fcmToken.equals(fcmTokenOptional.get().getToken())) { // 토큰이 있지만, 다르다면 재설정
            token = fcmTokenRepository.findByToken(fcmToken);
            token.regenerateToken(fcmToken);
        }

        return FCMTokenDto.of(token);
    }

    // 토큰 삭제
    // 로그아웃 시 요청
    public String deleteUserToken(String deviceId, Long userId) {
        User user = userLookupService.findUserById(userId);

        fcmTokenRepository.deleteByDeviceIdAndUser(deviceId, user);

        return "success";
    }

    private FCMToken createFCMToken(Long userId, String deviceId, String token) {
        // 유저 조회
        User user = userLookupService.findUserById(userId);

        FCMToken fcmToken = FCMToken.builder()
                .token(token)
                .deviceId(deviceId)
                .user(user)
                .build();
        return fcmTokenRepository.save(fcmToken);
    }

    private void deleteToken(FCMToken token) {
        fcmTokenRepository.delete(token);
    }

    private void deleteTokens(List<FCMToken> tokens) {
        fcmTokenRepository.deleteAll(tokens);
    }

    public void deleteExpiredTokens() {
        log.info("deleteExpiredTokens");
        List<FCMToken> expiredTokenList = fcmTokenRepository.findExpiredTokenList(LocalDateTime.now());

        deleteTokens(expiredTokenList);
    }
}
