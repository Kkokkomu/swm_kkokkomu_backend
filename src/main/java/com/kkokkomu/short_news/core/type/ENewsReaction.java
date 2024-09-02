package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ENewsReaction {
    LIKE("LIKE"),
    SURPRISE("SURPRISE"),
    SAD("SAD"),
    ANGRY("ANGRY");

    private final String newsReaction;
}
