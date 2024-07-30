package com.kkokkomu.short_news.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ELoginProvider {
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO"),
    APPLE("APPLE"),
    DEFAULT("DEFAULT");

    private final String loginProvider;
}
