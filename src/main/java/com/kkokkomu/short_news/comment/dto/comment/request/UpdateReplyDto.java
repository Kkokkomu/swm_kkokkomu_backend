package com.kkokkomu.short_news.comment.dto.comment.request;

public record UpdateReplyDto(
        Long replyId,
        String content
){
}
