package com.kkokkomu.short_news.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ECategory {
    POLITICS("POLITICS"),
    ECONOMY("ECONOMY"),
    SOCIAL("SOCIAL"),
    ENTERTAIN("ENTERTAIN"),
    SPORTS("SPORTS"),
    LIVING("LIVING"),
    WOLRD("WORLD"),
    IT("IT");

    private final String category;
}
