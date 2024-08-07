package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.annotation.UserId;
import com.kkokkomu.short_news.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.dto.comment.request.CreateReplyDto;
import com.kkokkomu.short_news.dto.comment.request.UpdateCommentDto;
import com.kkokkomu.short_news.dto.comment.request.UpdateReplyDto;
import com.kkokkomu.short_news.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.dto.comment.response.CommentListDto;
import com.kkokkomu.short_news.dto.comment.response.ReplyByParentDto;
import com.kkokkomu.short_news.dto.comment.response.ReplyDto;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    /* 댓글 */

    @Operation(summary = "댓글 추가")
    @PostMapping("")
    public ResponseDto<CommentDto> addComment(@Parameter(hidden = true) @UserId Long userId,
                                              @RequestBody CreateCommentDto createCommentDto) {
        log.info("addComment controller");
        return ResponseDto.ok(commentService.createComment(userId, createCommentDto));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("")
    public ResponseDto<String> deleteComment(@RequestParam Long commentId) {
        log.info("deleteComment controller");
        return ResponseDto.ok(commentService.deleteComment(commentId));
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("")
    public ResponseDto<String> editComment(@RequestBody UpdateCommentDto updateCommentDto) {
        log.info("editComment controller");
        return ResponseDto.ok(commentService.updateComment(updateCommentDto));
    }

    @Operation(summary = "최신순 댓글 조회")
    @GetMapping("/latest")
    public ResponseDto<List<CommentListDto>> readLatestComment(@RequestParam Long newsId,
                                                               @Parameter(description = "처음 조회 요청시에는 보내지 않나도됌. 두번째 요청부터 이전에 받은 데이터들 중 제일 마지막 댓글 id를 cursor id로 반환") @RequestParam(required = false) Long cursorId,
                                                               @RequestParam int size) {
        return ResponseDto.ok(commentService.readLatestComments(newsId, cursorId, size));
    }

    @Operation(summary = "인기순 댓글 조회")
    @GetMapping("/popular")
    public ResponseDto<List<CommentListDto>> readPopularComment(@RequestParam Long newsId,
                                                               @Parameter(description = "처음 조회 요청시에는 보내지 않나도됌. 두번째 요청부터 이전에 받은 데이터들 중 제일 마지막 댓글 id를 cursor id로 반환") @RequestParam(required = false) Long cursorId,
                                                               @RequestParam int size) {
        return ResponseDto.ok(commentService.readPopularComments(newsId, cursorId, size));
    }

    /* 대댓글 */

    @Operation(summary = "대댓글 추가")
    @PostMapping("/reply")
    public ResponseDto<ReplyDto> addReply(@Parameter(hidden = true) @UserId Long userId,
                                          @RequestBody CreateReplyDto createReplyDto) {
        log.info("addReply controller");
        return ResponseDto.ok(commentService.createReply(userId, createReplyDto));
    }

    @Operation(summary = "대댓글 삭제")
    @DeleteMapping("/reply")
    public ResponseDto<String> deleteReply(@RequestParam Long replyId) {
        log.info("deleteReply controller");
        return ResponseDto.ok(commentService.deleteReply(replyId));
    }

    @Operation(summary = "대댓글 수정")
    @PutMapping("/reply")
    public ResponseDto<String> editReply(@RequestBody UpdateReplyDto updateReplyDto) {
        log.info("editReply controller");
        return ResponseDto.ok(commentService.updateReply(updateReplyDto));
    }

    @Operation(summary = "오래된순 대댓글 조회")
    @GetMapping("/reply/oldest")
    public ResponseDto<ReplyByParentDto> readOldestComment(@RequestParam Long commentId,
                                                           @Parameter(description = "처음 조회 요청시에는 보내지 않나도됌. 두번째 요청부터 이전에 받은 데이터들 중 제일 마지막 댓글 id를 cursor id로 반환") @RequestParam(required = false) Long cursorId,
                                                           @RequestParam int size) {
        return ResponseDto.ok(commentService.readOldestReply(commentId, cursorId, size));
    }
}
