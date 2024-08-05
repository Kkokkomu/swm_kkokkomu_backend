package com.kkokkomu.short_news.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EHomeFilter {
    RECOMMAND("RECOMMAND"),
    LATEST("LATEST");

    private final String homeFileter;
}
