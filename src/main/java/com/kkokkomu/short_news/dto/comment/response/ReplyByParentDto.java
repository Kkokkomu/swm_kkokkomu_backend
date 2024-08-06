package com.kkokkomu.short_news.dto.comment.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ReplyByParentDto(
        CommentListDto parentComment,
        List<ReplyListDto> replies
) {
}
