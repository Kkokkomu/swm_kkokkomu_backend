package com.kkokkomu.short_news.dto.comment.request;

import lombok.NonNull;

public record UpdateCommentDto(
        @NonNull
        Long commentId,
        @NonNull
        String comment
) {
}
