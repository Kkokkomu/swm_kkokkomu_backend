package com.kkokkomu.short_news.user.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import static com.kkokkomu.short_news.core.constant.Constant.DEFAULT_PROFILE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profile_img", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class ProfileImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Foreign key to User entity

    @Column(name = "img_url", columnDefinition = "TEXT", nullable = false)
    private String imgUrl; // S3 URL

    @Column(name = "resize_url", columnDefinition = "TEXT")
    private String resizeUrl; // S3 Resizing Image URL

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일자, 객체 생성 시 자동 설정

    @Column(name = "edited_at")
    private LocalDateTime editedAt; // 변경 일자

    @Builder
    public ProfileImg(User user, String imgUrl, String resizeUrl) {
        this.user = user;
        this.imgUrl = imgUrl;
        this.resizeUrl = resizeUrl;
        this.createdAt = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 설정
        this.editedAt = LocalDateTime.now(); // 초기값을 현재 시간으로 설정
    }

    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now(); // 업데이트 시 변경 시간 갱신
    }

    public void putDefaultImg() {
        this.imgUrl = DEFAULT_PROFILE;
        this.resizeUrl = DEFAULT_PROFILE;
    }

    public void updateImg(String imgUrl) {
        this.imgUrl = imgUrl;
        this.resizeUrl = imgUrl;
        this.editedAt = LocalDateTime.now();
    }
}
