package com.kkokkomu.short_news.constant;

import java.util.List;

public class Constant {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String BASIC_PREFIX = "Basic ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION = "accessToken";
    public static final String REAUTHORIZATION = "refreshToken";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String USER_ROLE_CLAIM_NAME = "role";
    public static final String USER_ID_CLAIM_NAME = "uid";
    public static final String USER_EMAIL_CLAIM_NAME = "email";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final Long REPLY_WEIGHT = 3L;
    public static final Long LIKE_WEIGHT = 2L;
    public static final String VIDEO_SERVER_GENERATE_HOST = "http://43.201.54.121:8000/generate";
    public static final List<String> NO_NEED_AUTH_URLS = List.of(
            //테스트
            "/test/**",

            //스웨거
            "/swagger",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api-docs",
            "/api-docs/**",
            "/v3/api-docs/**",

            //소셜로그인
            "/oauth2/login/kakao",
            "/oauth2/login/google",
            "/oauth2/login/apple",

            // 홈화면
            "/news/list/guest",

            //댓글
            "/comment/latest/guest",
            "/comment/popular/guest",
            "/comment/reply/oldest/guest",

            //뉴스 탐색
            "/news/filter"
    );
    public static final String DEFAULT_PROFILE = "https://kkm-config.s3.ap-northeast-2.amazonaws.com/profile.png";
}
