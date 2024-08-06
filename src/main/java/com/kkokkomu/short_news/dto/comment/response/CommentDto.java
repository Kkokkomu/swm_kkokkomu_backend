package com.kkokkomu.short_news.dto.comment.response;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.User;
import lombok.Builder;

@Builder
public record CommentDto(
        Long id,
        Long userId,
        Long newsId,
        String content,
        String editedAt
) {
    static public CommentDto of(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .newsId(comment.getNews().getId())
                .content(comment.getContent())
                .editedAt(comment.getEditedAt().toString())
                .build();
    }
}
