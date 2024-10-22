package com.kkokkomu.short_news.alarm.domain;

import com.kkokkomu.short_news.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "alarm_setting")
public class AlarmSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // seq

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "generate_alarm_yn", nullable = false)
    private Boolean generateAlarmYn; // 새 뉴스 생 알림 on off

    @Column(name = "reply_alarm_yn", nullable = false)
    private Boolean replyAlarmYn; // 대댓글 알림 on off

    @Column(name = "ban_alarm_yn", nullable = false)
    private Boolean banAlarmYn; // 제재 대상 알림 on off

    @Column(name = "change_info_yn", nullable = false)
    private Boolean changeInfoYn; // 관심 팝업 정보 변경 알림 on off

}
