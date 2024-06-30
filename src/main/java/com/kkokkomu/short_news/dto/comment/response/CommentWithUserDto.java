package com.kkokkomu.short_news.dto.comment.response;

import lombok.Builder;

@Builder
public record CommentWithUserDto(
        Long commentId,
        String userProfileImg,
        Long userId,
        String createdAt,
        String comment,
        Long great
) {
}
