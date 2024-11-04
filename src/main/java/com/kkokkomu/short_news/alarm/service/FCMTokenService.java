package com.kkokkomu.short_news.alarm.service;

import com.kkokkomu.short_news.alarm.domain.FCMToken;
import com.kkokkomu.short_news.alarm.dto.fcm.response.FCMTokenDto;
import com.kkokkomu.short_news.alarm.repository.FCMTokenRepository;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
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
    public FCMTokenDto applyFCMToken(String token, Long userId) {
        log.info("Applying FCM token: {}", token);

        // FCM 토큰 저장
        FCMToken fcmToken = createFCMToken(userId, token);

        return FCMTokenDto.of(fcmToken);
    }

    // 토큰 update 필요 여부 검증 메서드, 앱진입, 로그인 시 사용
    public FCMTokenDto verifyFCMToken(Long userId, String fcmToken) {
        log.info("verify token : {}", fcmToken);

        // 토큰이 없거나 다르면 생성
        Optional<FCMToken> fcmTokenOptional = fcmTokenRepository.findByToken(fcmToken);

        FCMToken token;
        if (fcmTokenOptional.isEmpty()) { // 토큰이 비어있으면 새로 생성
            log.info("FCM token not found: {}", fcmToken);
            token = createFCMToken(userId, fcmToken);
        } else if (!fcmToken.equals(fcmTokenOptional.get().getToken())) { // 토큰이 있지만, 다르다면 재설정
            log.info("FCM token not match: {}", fcmToken);
            token = fcmTokenOptional.get();
            token.regenerateToken(fcmToken);
        } else { // 토큰이 있고, 기존과 같음
            log.info("FCM token match: {}", fcmToken);
            token = fcmTokenOptional.get();
        }

        return FCMTokenDto.of(token);
    }

    // 토큰 삭제
    // 로그아웃 시 요청
    @Transactional
    public String deleteUserToken(String fcmToken, Long userId) {
        log.info("Deleting user token: {}", fcmToken);

        if (!userLookupService.existsUser(userId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_USER);
        }

        fcmTokenRepository.deleteByToken(fcmToken);

        return "success";
    }

    private FCMToken createFCMToken(Long userId, String token) {
        log.info("Creating FCM token: {}", token);
        // 유저 조회
        User user = userLookupService.findUserById(userId);

        FCMToken fcmToken = FCMToken.builder()
                .token(token)
                .user(user)
                .build();
        return fcmTokenRepository.save(fcmToken);
    }

    @Transactional
    public void deleteToken(String fcmToken) {
        log.info("Deleting FCM token: {}", fcmToken);
        fcmTokenRepository.deleteByToken(fcmToken);
    }

    private void deleteFCMToken(FCMToken token) {
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
