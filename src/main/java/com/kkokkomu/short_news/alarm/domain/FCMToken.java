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
@Table(name = "fcm_token")
public class FCMToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, columnDefinition = "TINYTEXT")
    private String token; // 토큰, 최대 255자

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt; // 토큰 갱신일

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt; // 토큰 만료일

    @Builder
    public FCMToken(User user, String token){
        this.user = user;
        this.token      = token;
        this.editedAt    = LocalDateTime.now();
        this.expiredAt    = LocalDateTime.now().plusMonths(1);
    }

    // 토큰 갱신
    public void refreshToken() {
        this.editedAt = LocalDateTime.now();
        this.expiredAt = editedAt.plusMonths(1); // 1달 갱신
    }

    // 토큰 재생성
    public void regenerateToken(String fcmToken) {
        this.token = fcmToken;
        refreshToken();
    }
}

