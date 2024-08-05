package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.annotation.UserId;
import com.kkokkomu.short_news.dto.commentLike.request.CreateCommentLike;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.service.CommentLikeService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "댓글 좋아요 생성")
    @PostMapping("")
    public ResponseDto<String> addCommentLike(@UserId Long userId,
                                              @RequestBody CreateCommentLike createCommentLike) {
        log.info("addCommentLike controller");
        return ResponseDto.ok(commentLikeService.createCommentLike(userId, createCommentLike));
    }
}
