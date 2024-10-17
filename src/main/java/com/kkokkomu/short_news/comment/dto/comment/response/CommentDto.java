package com.kkokkomu.short_news.comment.dto.comment.response;

import com.kkokkomu.short_news.comment.domain.Comment;
import lombok.Builder;

@Builder
public record CommentDto(
        Long id,
        Long userId,
        Long newsId,
        String content,
        String editedAt,
        Long parentId
) {
    static public CommentDto of(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .newsId(comment.getNews().getId())
                .content(comment.getContent())
                .editedAt(comment.getEditedAt().toString())
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .build();
    }
}
