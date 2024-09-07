package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ENewsProgress {
    UNEXECUTED("UNEXECUTED"),
    EXECUTING("EXECUTING"),
    BANISHED("BANISHED"),
    DELETED("DELETED");

    private final String progressProvider;
}