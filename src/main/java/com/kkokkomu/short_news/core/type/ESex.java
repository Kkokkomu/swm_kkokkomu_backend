package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ESex {
    MAN("MAN"),
    WOMAN("WOMAN"),
    NONE("NONE");

    private final String sex;
}
