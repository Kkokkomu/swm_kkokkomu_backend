package com.kkokkomu.short_news.alarm.domain;

import com.kkokkomu.short_news.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "notification_token")
public class FCMToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "token", nullable = false, columnDefinition = "TINYTEXT")
    private String token; // 토큰, 최대 255자

    @Column(name = "modify_date", nullable = false)
    private LocalDateTime mod_dtm; // 토큰 갱신일

    @Column(name = "expired_date", nullable = false)
    private LocalDateTime exp_dtm; // 토큰 만료일

    @Column(name = "device" , nullable = false)
    private String device;         // android or ios

    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId;

    @Builder
    public FCMToken(String token, LocalDateTime mod_dtm , String device, String deviceId ){
        this.token      = token;
        this.mod_dtm    = mod_dtm;
        this.exp_dtm    = mod_dtm.plusMonths(1);
        this.device     = device;
        this.deviceId   = deviceId;
    }

    // 토큰 갱신
    public void refreshToken() {
        this.mod_dtm = LocalDateTime.now();
        this.exp_dtm = mod_dtm.plusMonths(1); // 1달 갱신
    }

    // 토큰 재생성
    public void regenerateToken(FCMToken fcmToken) {
        this.token = fcmToken.getToken();
        refreshToken();
    }
}

