package com.kkokkomu.short_news.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EHomeFilter {
    RECOMMEND("RECOMMEND"),
    LATEST("LATEST");

    private final String homeFilter;
}
