package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public User(String uuid, String profileImg) {
        this.uuid = uuid;
        this.profileImg = profileImg;
        this.createdAt = LocalDateTime.now();
    }
}

