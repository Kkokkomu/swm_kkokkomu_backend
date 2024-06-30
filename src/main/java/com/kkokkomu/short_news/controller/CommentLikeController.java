package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.dto.commentLike.request.CreateCommentLikeDto;
import com.kkokkomu.short_news.dto.commentLike.response.CommentLikeDto;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/commentLike")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @PostMapping("")
    public ResponseDto<?> addCommentLike(@RequestBody CreateCommentLikeDto createCommentLikeDto) {
        log.info("controller addCommentLike");
        return ResponseDto.ok(commentLikeService.addCommentLike(createCommentLikeDto));
    } // 댓글 좋아요 생성

    @PostMapping("/delete")
    public ResponseDto<?> deleteCommentLike(@RequestBody CreateCommentLikeDto createCommentLikeDto) {
        log.info("controller deleteCommentLike");
        return ResponseDto.ok(commentLikeService.deleteCommentLike(createCommentLikeDto));
    } // 댓글 좋아요 삭제
}
