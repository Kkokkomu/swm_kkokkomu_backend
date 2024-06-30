package com.kkokkomu.short_news.dto.commentLike.request;

public record CreateCommentLikeDto(
    String userId,
    Long commentId
) {
}
