package com.kkokkomu.short_news.alarm.repository;

import com.kkokkomu.short_news.alarm.domain.FCMToken;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {

    // 만기된 토큰들 조회
    @Query("SELECT token FROM FCMToken token WHERE token.expiredAt <= :now")
    List<FCMToken> findExpiredTokenList(LocalDateTime now);


    Optional<FCMToken> findByToken(String token);

    void deleteByToken(String token);

    List<FCMToken> findByUser(User user);

    // 야간알림, 새 뉴스 알림 동의 토큰 조회
    @Query("SELECT f FROM FCMToken f WHERE f.user.nightAlarmYn = true and f.user.alarmNewContentYn = true")
    List<FCMToken> findAllByNightYnTrueAndNewContentYnTrue();

    // 새 뉴스 알림 동의 토큰 조회
    @Query("SELECT f FROM FCMToken f WHERE f.user.alarmNewContentYn = true")
    List<FCMToken> findAllByNewContentYnTrue();

    // 야간 알림, 공지 알람 동의 토큰 조회
    @Query("SELECT f FROM FCMToken f WHERE f.user.nightAlarmYn = true and f.user.alarmInformYn = true")
    List<FCMToken> findAllByNightYnTrueAndNotificationYnTrue();

    // 공지 알람 동의 토큰 조회
    @Query("SELECT f FROM FCMToken f WHERE f.user.alarmInformYn = true")
    List<FCMToken> findAllByNotificationYnTrue();
}

