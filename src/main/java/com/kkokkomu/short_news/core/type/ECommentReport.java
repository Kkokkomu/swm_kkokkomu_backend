package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ECommentReport {
    OFFENSIVE("OFFENSIVE"),
    PORNO("PORNO"),
    VIOLENT("VIOLENT"),
    PROFANE("PROFANE"),
    SPAM("SPAM");

    private final String commentReport;
}
