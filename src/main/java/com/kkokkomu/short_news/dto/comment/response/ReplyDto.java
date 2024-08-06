package com.kkokkomu.short_news.dto.comment.response;

import com.kkokkomu.short_news.domain.Comment;
import lombok.Builder;

@Builder
public record ReplyDto(
        Long id,
        Long userId,
        Long newsId,
        Long parentId,
        String content,
        String editedAt
) {
    static public ReplyDto of(Comment comment) {
        return ReplyDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .newsId(comment.getNews().getId())
                .parentId(comment.getParent().getId())
                .content(comment.getContent())
                .editedAt(comment.getEditedAt().toString())
                .build();
    }
}
