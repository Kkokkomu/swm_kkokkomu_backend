package com.kkokkomu.short_news.dto.comment.response;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.User;
import lombok.Builder;

@Builder
public record CommentDto(
        Long userId,
        Long newsId,
        String content,
        String createdAt,
        String editedAt
) {
    static public CommentDto fromEntity(Comment comment) {
        return CommentDto.builder()
                .userId(comment.getUser().getId())
                .newsId(comment.getNews().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toString())
                .editedAt(comment.getEditedAt().toString())
                .build();
    }
}
