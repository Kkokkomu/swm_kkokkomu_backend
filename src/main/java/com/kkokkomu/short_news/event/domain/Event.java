package com.kkokkomu.short_news.event.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @Column(name = "title", nullable = false)
    private String title; // 이벤트 제목

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // 이벤트 본문

    @Column(name = "logo_image", columnDefinition = "TEXT")
    private String logoImage; // 로고 이미지 URL

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일자

    @Column(name = "edited_at")
    private LocalDateTime editedAt; // 수정 일자

    @Builder
    public Event(String title, String content, String logoImage) {
        this.title = title;
        this.content = content;
        this.logoImage = logoImage;
        this.createdAt = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 설정
        this.editedAt = LocalDateTime.now(); // 초기값을 현재 시간으로 설정
    }

    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now(); // 업데이트 시 변경 시간 갱신
    }
}
