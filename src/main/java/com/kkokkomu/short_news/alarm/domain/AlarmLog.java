package com.kkokkomu.short_news.alarm.domain;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.core.type.EAlarmType;
import com.kkokkomu.short_news.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "alarm_log")
public class AlarmLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "receiver", nullable = false)
    private User receiver;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "alarm_type", nullable = false)
    private EAlarmType alarmType;

    @OneToOne
    @JoinColumn(name = "comment")
    private Comment comment;

    @OneToOne
    @JoinColumn(name = "notification")
    private Notification notification;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public AlarmLog(User receiver, EAlarmType alarmType, Comment comment, Notification notification) {
        this.receiver = receiver;
        this.alarmType = alarmType;
        this.comment = comment;
        this.notification = notification;
        this.isRead = false;
        this.editedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now(); // 업데이트 시 변경 시간 갱신
    }
}

