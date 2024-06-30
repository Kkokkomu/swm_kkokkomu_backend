package com.kkokkomu.short_news.dto.comment.response;

import lombok.Builder;

@Builder
public record CommentWithUserAll(
        String userProfileImg,
        Long userId,
        String createdAt,
        String comment,
        Long great
) {
}
