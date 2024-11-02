package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ECategory {
    POLITICS("POLITICS"),
    ECONOMY("ECONOMY"),
    SOCIAL("SOCIAL"),
    ENTERTAIN("ENTERTAIN"),
    SPORTS("SPORTS"),
    LIVING("LIVING"),
    WORLD("WORLD"),
    IT("IT"),
    HEADLINE("HEADLINE");

    private final String category;
}
