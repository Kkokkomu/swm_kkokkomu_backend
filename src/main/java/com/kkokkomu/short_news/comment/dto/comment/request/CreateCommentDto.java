package com.kkokkomu.short_news.comment.dto.comment.request;

import jakarta.validation.constraints.NotNull;

public record CreateCommentDto(
        @NotNull Long newsId,
        @NotNull String content
) {
}
