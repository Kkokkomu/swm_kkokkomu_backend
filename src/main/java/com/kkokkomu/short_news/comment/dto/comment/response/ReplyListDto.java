package com.kkokkomu.short_news.comment.dto.comment.response;

import com.kkokkomu.short_news.user.dto.user.response.CommentSummoryDto;
import lombok.Builder;

@Builder
public record ReplyListDto(
        CommentSummoryDto user,
        CommentDto comment,
        Long commentLikeCnt,
        Boolean userLike
) {
}
