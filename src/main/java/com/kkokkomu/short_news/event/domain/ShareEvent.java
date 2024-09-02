package com.kkokkomu.short_news.event.domain;

import com.kkokkomu.short_news.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "share_event", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class ShareEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Foreign key to User entity

    @Column(name = "recommand_code", nullable = false)
    private String recommandCode; // 추천인 코드

    @Column(name = "reward_granted", nullable = false)
    private boolean rewardGranted; // 이미 보상을 받았는지 여부

    @Column(name = "participating_cnt", nullable = false)
    private int participatingCnt; // 얼마나 초대했는지 (참여한 인원 수)

    @Column(name = "edited_at")
    private LocalDateTime editedAt; // 변경 일자

    @Builder
    public ShareEvent(User user, String recommandCode, boolean rewardGranted, int participatingCnt) {
        this.user = user;
        this.recommandCode = recommandCode;
        this.rewardGranted = rewardGranted;
        this.participatingCnt = participatingCnt;
        this.editedAt = LocalDateTime.now(); // 초기값을 현재 시간으로 설정
    }

    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now(); // 업데이트 시 변경 시간 갱신
    }

    public void updateParticipantingCnt() {
        this.participatingCnt += 1;
    }
}
