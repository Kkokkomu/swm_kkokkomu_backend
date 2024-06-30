package com.kkokkomu.short_news.dto.commentLike.response;

import com.kkokkomu.short_news.domain.CommentLike;
import lombok.Builder;

@Builder
public record CommentLikeDto(
        Long userId,
        Long commentId,
        String createdAt
) {
    static public CommentLikeDto fromEntity(CommentLike commentLike) {
        return CommentLikeDto.builder()
                .userId(commentLike.getUser().getId())
                .commentId(commentLike.getComment().getId())
                .createdAt(commentLike.getCreatedAt().toString())
                .build();
    }
}
