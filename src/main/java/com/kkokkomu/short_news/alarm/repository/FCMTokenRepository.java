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

    List<FCMToken> findAllByDeviceId(String deviceId);

    Optional<FCMToken> findByToken(String token);

    Optional<FCMToken> findByDeviceIdAndToken(String deviceId, String token);

    FCMToken findByDeviceIdAndUserId(String deviceId, Long userId);

    void deleteByDeviceIdAndUser(String deviceId, User user);

    void deleteByToken(String token);
}
