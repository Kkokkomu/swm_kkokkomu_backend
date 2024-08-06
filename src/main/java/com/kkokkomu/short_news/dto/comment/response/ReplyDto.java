package com.kkokkomu.short_news.dto.comment.response;

import com.kkokkomu.short_news.domain.Comment;
import lombok.Builder;

import java.util.List;

@Builder
public record ReplyDto(
        CommentListDto parentComment,
        List<ReplyListDto> replies
) {
}
