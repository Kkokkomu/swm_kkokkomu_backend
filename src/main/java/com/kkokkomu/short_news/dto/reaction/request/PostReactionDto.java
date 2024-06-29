package com.kkokkomu.short_news.dto.reaction.request;

import lombok.Builder;
import lombok.NonNull;

public record PostReactionDto(
        @NonNull
        String userId,
        @NonNull
        Long newsId,
        @NonNull
        Boolean great,
        @NonNull
        Boolean hate,
        @NonNull
        Boolean expect,
        @NonNull
        Boolean surprise
) {
}
