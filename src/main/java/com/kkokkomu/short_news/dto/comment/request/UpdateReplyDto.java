package com.kkokkomu.short_news.dto.comment.request;

public record UpdateReplyDto(
        Long replyId,
        String content
){
}
