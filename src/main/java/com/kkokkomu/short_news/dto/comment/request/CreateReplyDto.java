package com.kkokkomu.short_news.dto.comment.request;

import jakarta.validation.constraints.NotNull;

public record CreateReplyDto(
        @NotNull Long newsId,
        @NotNull Long commentId,
        @NotNull String content
) {
}
