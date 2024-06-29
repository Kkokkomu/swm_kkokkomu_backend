package com.kkokkomu.short_news.dto.reaction.response;

import lombok.Builder;

@Builder
public record ReactionCntDto(
        Long great,
        Long hate,
        Long expect,
        Long surprise
) {
    @Builder
    public ReactionCntDto(Long great, Long hate, Long expect, Long surprise) {
        this.great = great;
        this.hate = hate;
        this.expect = expect;
        this.surprise = surprise;
    }
}
