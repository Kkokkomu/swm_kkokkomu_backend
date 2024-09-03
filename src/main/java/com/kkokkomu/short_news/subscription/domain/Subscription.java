package com.kkokkomu.short_news.subscription.domain;

import com.kkokkomu.short_news.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "subscription_hist")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium; // 프리미엄 여부

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date") // 무기한 구독 가능
    private LocalDateTime endDate;

    @Builder
    public Subscription(User user, Boolean isPremium, LocalDateTime startDate, LocalDateTime endDate) {
        this.user = user;
        this.isPremium = isPremium;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
