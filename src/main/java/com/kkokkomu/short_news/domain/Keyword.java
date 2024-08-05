package com.kkokkomu.short_news.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "keyword")
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @Column(name = "keyword", nullable = false, unique = true)
    private String keyword; // 키워드

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일자

    @Builder
    public Keyword(String keyword) {
        this.keyword = keyword;
        this.createdAt = LocalDateTime.now();
    }
}
