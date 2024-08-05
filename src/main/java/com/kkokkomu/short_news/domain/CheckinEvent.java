package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "checkin_event", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class CheckinEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Foreign key to User entity

    @Column(name = "consecutive_checkins", nullable = false)
    private int consecutiveCheckins; // 연속 출석 횟수

    @Column(name = "last_checkin_date", nullable = false)
    private LocalDateTime lastCheckinDate; // 마지막 출석 날짜

    @Column(name = "reward_granted", nullable = false)
    private boolean rewardGranted; // 이미 보상을 받았는지 여부

    @Builder
    public CheckinEvent(User user, int consecutiveCheckins, LocalDateTime lastCheckinDate, boolean rewardGranted) {
        this.user = user;
        this.consecutiveCheckins = consecutiveCheckins;
        this.lastCheckinDate = lastCheckinDate;
        this.rewardGranted = rewardGranted;
    }
}
