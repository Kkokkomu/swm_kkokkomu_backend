package com.kkokkomu.short_news.comment.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.comment.dto.commentLike.request.CreateCommentLike;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.comment.service.CommentLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 좋아요")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/comment/like")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @Operation(summary = "댓글 및 대댓글 좋아요 생성")
    @PostMapping("")
    public ResponseDto<String> addCommentLike(@Parameter(hidden = true) @UserId Long userId,
                                              @RequestBody CreateCommentLike createCommentLike) {
        log.info("addCommentLike controller");
        return ResponseDto.ok(commentLikeService.createCommentLike(userId, createCommentLike));
    }

    @Operation(summary = "댓글 및 대댓글 좋아요 삭제")
    @DeleteMapping("")
    public ResponseDto<String> deleteCommentLike(@Parameter(hidden = true) @UserId Long userId,
                                                @RequestParam Long commentId) {
        return ResponseDto.ok(commentLikeService.deleteCommentLike(userId, commentId));
    }
}
