package com.kkokkomu.short_news.dto.comment.response;

import com.kkokkomu.short_news.dto.user.response.CommentSummoryDto;
import lombok.Builder;

@Builder
public record ReplyListDto(
        CommentSummoryDto user,
        CommentDto comment,
        Long commentLikeCnt
) {
}
