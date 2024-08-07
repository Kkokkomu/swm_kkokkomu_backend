package com.kkokkomu.short_news.dto.comment.response;

import com.kkokkomu.short_news.dto.user.response.CommentSummoryDto;
import lombok.Builder;

@Builder
public record CommentListDto(
        CommentSummoryDto user,
        CommentDto comment,
        int replyCnt,
        Long commentLikeCnt
) {
}
