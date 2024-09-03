package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EHomeFilter {
    RECOMMEND("RECOMMEND"),
    LATEST("LATEST");

    private final String homeFilter;
}
