package com.kkokkomu.short_news.dto.comment.request;

import lombok.NonNull;

public record CreateCommentDto(
        String userId,
        Long newsId,
        String comment
) {
}
