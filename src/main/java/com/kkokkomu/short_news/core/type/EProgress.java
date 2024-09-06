package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EProgress {
    UNEXECUTED("UNEXECUTED"),
    EXECUTING("EXECUTING"),
    EXECUTED("EXECUTED");

    private final String progressProvider;
}