package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EAndroidChannelId {
    NOTICE("NOTICE"),
    REPLY("REPLY"),
    NEWS_ARTICLE("NEWS_ARTICLE"),
    GENERAL("GENERAL");

    private final String channelId;
}
