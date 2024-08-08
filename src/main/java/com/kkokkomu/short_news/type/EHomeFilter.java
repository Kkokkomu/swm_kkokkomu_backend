package com.kkokkomu.short_news.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EHomeFilter {
    RECOMMAND("RECOMMEND"),
    LATEST("LATEST");

    private final String homeFileter;
}
