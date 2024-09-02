package com.kkokkomu.short_news.dto.comment.response;

import com.kkokkomu.short_news.dto.user.response.CommentSummoryDto;
import lombok.Builder;

@Builder
public record GuestReplyListDto(
        CommentSummoryDto user,
        CommentDto comment,
        Long commentLikeCnt
) {
}
