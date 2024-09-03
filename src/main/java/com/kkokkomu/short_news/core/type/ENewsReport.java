package com.kkokkomu.short_news.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ENewsReport {
    PORNO("PORNO"),
    VIOLENT("VIOLENT"),
    MISINFORMATION("MISINFORMATION"),
    LEGAL("LEGAL"),
    SPAM("SPAM");

    private final String newsReport;
}
