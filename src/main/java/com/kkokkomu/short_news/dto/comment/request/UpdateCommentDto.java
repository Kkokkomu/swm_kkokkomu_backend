package com.kkokkomu.short_news.dto.comment.request;

public record UpdateCommentDto (
        Long commentId,
        String content
){
}
