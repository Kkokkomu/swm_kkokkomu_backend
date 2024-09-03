package com.kkokkomu.short_news.comment.dto.comment.request;

public record UpdateCommentDto (
        Long commentId,
        String content
){
}
