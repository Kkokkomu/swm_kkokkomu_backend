package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EAlarmType {
    NOTICE("NOTICE"),
    REPLY("REPLY"),
    NEWS_ARTICLE("NEWS_ARTICLE"),
    TEST("TEST");

    private final String alarmType;
}
