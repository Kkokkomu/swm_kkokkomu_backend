package com.kkokkomu.short_news.dto.reaction.response;

import lombok.Builder;

public record ReactionWithUser(
        Boolean great,
        Boolean hate,
        Boolean expect,
        Boolean surprise
) {
    @Builder
    public ReactionWithUser(Boolean great, Boolean hate, Boolean expect, Boolean surprise) {
        this.great = great;
        this.hate = hate;
        this.expect = expect;
        this.surprise = surprise;
    }
}
