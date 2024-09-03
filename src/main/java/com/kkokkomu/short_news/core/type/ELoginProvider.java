package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ELoginProvider {
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO"),
    APPLE("APPLE");

    private final String loginProvider;
}
