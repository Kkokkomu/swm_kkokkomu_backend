package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event_img", indexes = {
        @Index(name = "idx_event_id", columnList = "event_id")
})
public class EventImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; // Foreign key to Event entity

    @Column(name = "img_url", columnDefinition = "TEXT", nullable = false)
    private String imgUrl; // 이미지 URL

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일자

    @Column(name = "edited_at")
    private LocalDateTime editedAt; // 수정 일자

    @Builder
    public EventImg(Event event, String imgUrl) {
        this.event = event;
        this.imgUrl = imgUrl;
        this.createdAt = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 설정
        this.editedAt = LocalDateTime.now(); // 초기값을 현재 시간으로 설정
    }

    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now(); // 업데이트 시 변경 시간 갱신
    }
}
