package com.kkokkomu.short_news.news.domain;

import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.keyword.domain.NewsKeyword;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @Column(name = "shortform_url", columnDefinition = "TEXT")
    private String shortformUrl; // 숏폼 URL

    @Column(name = "youtube_url", columnDefinition = "TEXT")
    private String youtubeUrl; // YouTube URL

    @Column(name = "instagram_url", columnDefinition = "TEXT")
    private String instagramUrl; // Instagram URL

    @Column(name = "thumbnail", columnDefinition = "TEXT")
    private String thumbnail; // 썸네일 이미지 링크

    @Column(name = "view_cnt", nullable = false)
    private int viewCnt; // 조회수

    @Column(name = "title")
    private String title; // 제목

    @Column(name = "summary")
    private String summary; // 요약 스크립트

    @Column(name = "shared_cnt", nullable = false)
    private int sharedCnt; // 공유수

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private ECategory category; // 카테고리

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일자

    @Column(name = "edited_at")
    private LocalDateTime editedAt; // 변경 일자

    @Column(name = "related_url")
    private String relatedUrl; // 관련 URL

    @Column(name = "score", nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double score; // 인기순 점수

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // 댓글들

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsReaction> reactions = new ArrayList<>();; // 감정표현

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsViewHist> newsViewHists = new ArrayList<>();; // 뉴스 시청 기록

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsKeyword> newsKeywords = new ArrayList<>();; // 뉴스 키워드 매핑

    @Builder
    public News(String shortformUrl, String youtubeUrl, String instagramUrl, String relatedUrl, String thumbnail, String title, String summary, ECategory category) {
        this.shortformUrl = shortformUrl;
        this.youtubeUrl = youtubeUrl;
        this.instagramUrl = instagramUrl;
        this.relatedUrl = relatedUrl;
        this.thumbnail = thumbnail;
        this.viewCnt = 0;
        this.title = title;
        this.summary = summary;
        this.sharedCnt = 0;
        this.category = category;
        this.createdAt = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 설정
        this.editedAt = LocalDateTime.now(); // 초기값을 현재 시간으로 설정
        this.score = 0.0;
    }

    public void update(String shortformUrl, String youtubeUrl, String instagramUrl, String relatedUrl, String thumbnail, String title, String summary, ECategory category) {
        this.shortformUrl = shortformUrl;
        this.youtubeUrl = youtubeUrl;
        this.instagramUrl = instagramUrl;
        this.relatedUrl = relatedUrl;
        this.thumbnail = thumbnail;
        this.title = title;
        this.summary = summary;
        this.category = category;
    }

    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now(); // 업데이트 시 변경 시간 갱신
    }

    public void incrementViewCnt() {
        this.viewCnt += 1;
    }

    public void updateSharedCnt() {
        this.sharedCnt++;
    }

    public void updateScore(Double score) {
        this.score = score;
    }

    public void addScore(Double score) {
        this.score = this.score + score;
    }

    public void updateViewCnt(int viewCnt) {
        this.viewCnt += viewCnt;
    }
}
