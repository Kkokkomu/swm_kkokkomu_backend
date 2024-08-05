package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.annotation.UserId;
import com.kkokkomu.short_news.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.userCategory.request.UpdateUserCategoryDto;
import com.kkokkomu.short_news.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 추가")
    @PostMapping("")
    public ResponseDto<CommentDto> addComment(@Parameter(hidden = true) @UserId Long userId,
                                              @RequestBody CreateCommentDto createCommentDto) {
        log.info("addComment controller");
        return ResponseDto.ok(commentService.addComment(userId, createCommentDto));
    }
}
