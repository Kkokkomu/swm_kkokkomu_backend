package com.kkokkomu.short_news.dto.comment.request;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.User;
import jakarta.validation.constraints.NotNull;

public record CreateCommentDto(
        @NotNull Long newsId,
        @NotNull String content
) {
}
