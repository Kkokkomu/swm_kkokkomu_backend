package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.dto.comment.request.UpdateCommentDto;
import com.kkokkomu.short_news.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    } // 댓글 작성

    @GetMapping("/list")
    public ResponseDto<?> listComments(@RequestParam Long newsId,
                                       @RequestParam int page,
                                       @RequestParam int size) {
        log.info("List comments");
        return ResponseDto.ok(commentService.readCommentList(newsId, page, size));
    } // 댓글 리스트 조회

    @PutMapping("")
    public ResponseDto<?> updateComment(@RequestBody UpdateCommentDto commentDto) {
        log.info("Update comment {}", commentDto);
        return ResponseDto.ok(commentService.updateComment(commentDto));
    } // 댓글 수정

    @DeleteMapping("")
    public ResponseDto<?> deleteComment(@RequestParam Long comment) {
        log.info("Delete comment {}", comment);
        return ResponseDto.ok(commentService.deleteComment(comment));
    }
}
