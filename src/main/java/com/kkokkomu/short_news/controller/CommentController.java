package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("")
    public ResponseDto<?> addComment(@RequestBody CreateCommentDto commentDto) {
        log.info("Add comment {}", commentDto);
        return ResponseDto.ok(commentService.saveComment(commentDto));
    }
}
