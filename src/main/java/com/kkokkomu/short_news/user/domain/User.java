package com.kkokkomu.short_news.user.domain;

import com.kkokkomu.short_news.core.oauth2.OAuth2UserInfo;
import com.kkokkomu.short_news.core.type.ELoginProvider;
import com.kkokkomu.short_news.core.type.ESex;
import com.kkokkomu.short_news.core.type.EUserRole;
import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

    private static final Logger log = LoggerFactory.getLogger(User.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @Column(name = "email", nullable = false, length = 320)
    private String email; // 최대 320자

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname")
    private String nickname; // 닉네임

    @Column(name = "birthday")
    private LocalDate birthday; // 생일

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private ESex sex; // 성별

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private EUserRole role;

    @Column(name = "login_provider")
    @Enumerated(EnumType.STRING)
    private ELoginProvider loginProvider;

    @Column(name = "is_login")
    private Boolean isLogin; // 로그인 상태, 세션으로 검사 필요

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken; // 리프레쉬 토큰

    @Column(name = "reported_cnt", nullable = false)
    private int reportedCnt; // 댓글 경고 횟수

    @Column(name = "banned_start_at")
    private LocalDateTime bannedStartAt; // 제제 시작 일시

    @Column(name = "banned_end_at")
    private LocalDateTime bannedEndAt; // 제제 종료 일시

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 최종 삭제 예정 일자

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted; // 소프트 삭제 여부

    @Column(name = "privacy_policy_yn")
    private Boolean privacyPolicyYn; // 개인정보 처리 방침 동의 여부

    @Column(name = "service_terms_yn")
    private Boolean serviceTermsYn; // 이용 약관 동의 여부

    @Column(name = "alarm_yn")
    private Boolean alarmYn; // 푸시알림 여부

    @Column(name = "alarm_new_content_yn")
    private Boolean alarmNewContentYn; // 새 뉴스 알림 여부

    @Column(name = "alarm_reply_yn")
    private Boolean alarmReplyYn; // 대댓글 알림 여부

    @Column(name = "alarm_ad_yn")
    private Boolean alarmAdYn; // 광고 알림 여부

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일자, 객체 생성 시 자동 설정

    @Column(name = "edited_at")
    private LocalDateTime editedAt; // 변경 일자

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileImg> profileImgs;

    @Builder
    public User(String email, String password, String nickname, LocalDate birthday, ESex sex, EUserRole role, ELoginProvider loginProvider, Boolean isLogin, String refreshToken, LocalDateTime bannedStartAt, LocalDateTime bannedEndAt, LocalDateTime deletedAt, Boolean isDeleted, Boolean privacyPolicyYn, Boolean serviceTermsYn, Boolean alarmYn, Boolean alarmNewContentYn, Boolean alarmReplyYn, Boolean alarmAdYn) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.birthday = birthday;
        this.sex = sex;
        this.role = role;
        this.loginProvider = loginProvider;
        this.isLogin = false;
        this.refreshToken = null;
        this.reportedCnt = 0;
        this.bannedStartAt = null;
        this.bannedEndAt = null;
        this.deletedAt = null;
        this.isDeleted = false;
        this.privacyPolicyYn = privacyPolicyYn;
        this.serviceTermsYn = serviceTermsYn;
        this.alarmYn = alarmYn;
        this.alarmNewContentYn = alarmNewContentYn;
        this.alarmReplyYn = alarmReplyYn;
        this.alarmAdYn = alarmAdYn;
        this.createdAt = LocalDateTime.now(); // 객체 생성 시 현재 시간으로 설정
        this.editedAt = LocalDateTime.now(); // 초기값을 현재 시간으로 설정
    }

    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now(); // 업데이트 시 변경 시간 갱신
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static User toGuestEntity(OAuth2UserInfo oAuth2UserInfo, String encodedPassword, ELoginProvider loginProvider) {
        return User.builder()
                .email(oAuth2UserInfo.email())
                .password(encodedPassword)
                .loginProvider(loginProvider)
                .role(EUserRole.GUEST)
                .privacyPolicyYn(true)
                .serviceTermsYn(true)
                .alarmYn(false)
                .alarmNewContentYn(false)
                .alarmReplyYn(false)
                .alarmAdYn(false)
                .isDeleted(false)
                .build();
    }

    public void register(String nickname, ESex sex, LocalDate birthday) {
        this.nickname = nickname;
        this.sex = sex;
        this.birthday = birthday;
        this.role = EUserRole.USER;
    }

    public void executeAboutComment() {
        log.info(String.valueOf(this.reportedCnt));
        this.reportedCnt++;
        log.info(String.valueOf(this.reportedCnt));


        if (this.reportedCnt >= 3) {
            this.bannedStartAt = LocalDateTime.now();
            this.bannedEndAt = LocalDateTime.now().plusDays(3);

            this.reportedCnt = 0;
        }
    }
}
