package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ENewsProgress {
    UNEXECUTED("UNEXECUTED"),
    EXECUTING("EXECUTING"),
    EXECUTED("EXECUTED"),
    DISMISSED("DISMISSED");

    private final String progressProvider;
}