package com.kkokkomu.short_news.comment.controller;

import com.kkokkomu.short_news.comment.dto.comment.response.*;
import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.comment.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.comment.dto.comment.request.CreateReplyDto;
import com.kkokkomu.short_news.comment.dto.comment.request.UpdateCommentDto;
import com.kkokkomu.short_news.comment.dto.comment.request.UpdateReplyDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "{\n" +
                    "    \"success\": false,\n" +
                    "    \"data\": null,\n" +
                    "    \"error\": {\n" +
                    "        \"code\": \"40029\",\n" +
                    "        \"message\": \"댓글 기능이 정지된 유저입니다.\"\n" +
                    "    }\n" +
                    "}",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))),
    })
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
    public ResponseDto<CursorResponseDto<List<CommentListDto>>> readLatestComment(@Parameter(hidden = true) @UserId Long userId,
                                                                                  @RequestParam Long newsId,
                                                                                  @Parameter(description = "처음 조회 요청시에는 보내지 않나도됌. 두번째 요청부터 이전에 받은 데이터들 중 제일 마지막 댓글 id를 cursor id로 반환") @RequestParam(required = false) Long cursorId,
                                                                                  @RequestParam int size) {
        return ResponseDto.ok(commentService.readLatestComments(userId, newsId, cursorId, size));
    }

    @Operation(summary = "인기순 댓글 조회")
    @GetMapping("/popular")
    public ResponseDto<CursorResponseDto<List<CommentListDto>>> readPopularComment(@Parameter(hidden = true) @UserId Long userId,
                                                                                   @RequestParam Long newsId,
                                                                                   @Parameter(description = "처음 조회 요청시에는 보내지 않나도됌. 두번째 요청부터 이전에 받은 데이터들 중 제일 마지막 댓글 id를 cursor id로 반환") @RequestParam(required = false) Long cursorId,
                                                                                   @RequestParam int size) {
        return ResponseDto.ok(commentService.readPopularComments(userId, newsId, cursorId, size));
    }

    @Operation(summary = "비로그인 최신순 댓글 조회")
    @GetMapping("/latest/guest")
    public ResponseDto<CursorResponseDto<List<GuestCommentListDto>>> guestReadLatestComment(@RequestParam Long newsId,
                                                                                            @Parameter(description = "처음 조회 요청시에는 보내지 않나도됌. 두번째 요청부터 이전에 받은 데이터들 중 제일 마지막 댓글 id를 cursor id로 반환") @RequestParam(required = false) Long cursorId,
                                                                                            @RequestParam int size) {
        return ResponseDto.ok(commentService.guestReadLatestComments(newsId, cursorId, size));
    }

    @Operation(summary = "비로그인 인기순 댓글 조회")
    @GetMapping("/popular/guest")
    public ResponseDto<CursorResponseDto<List<GuestCommentListDto>>> guestReadPopularComment(@RequestParam Long newsId,
                                                                                   @Parameter(description = "처음 조회 요청시에는 보내지 않나도됌. 두번째 요청부터 이전에 받은 데이터들 중 제일 마지막 댓글 id를 cursor id로 반환") @RequestParam(required = false) Long cursorId,
                                                                                   @RequestParam int size) {
        return ResponseDto.ok(commentService.guestReadPopularComments(newsId, cursorId, size));
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
    public ResponseDto<CursorResponseDto<List<ReplyListDto>>> readOldestComment(@Parameter(hidden = true) @UserId Long userId,
                                                                                @RequestParam Long commentId,
                                                                                @Parameter(description = "처음 조회 요청시에는 보내지 않나도됌. 두번째 요청부터 이전에 받은 데이터들 중 제일 마지막 댓글 id를 cursor id로 반환") @RequestParam(required = false) Long cursorId,
                                                                                @RequestParam int size) {
        return ResponseDto.ok(commentService.readOldestReply(userId, commentId, cursorId, size));
    }

    @Operation(summary = "비로그인 오래된순 대댓글 조회")
    @GetMapping("/reply/oldest/guest")
    public ResponseDto<CursorResponseDto<List<ReplyListDto>>> guestReadOldestComment(@RequestParam Long commentId,
                                                                               @Parameter(description = "처음 조회 요청시에는 보내지 않나도됌. 두번째 요청부터 이전에 받은 데이터들 중 제일 마지막 댓글 id를 cursor id로 반환") @RequestParam(required = false) Long cursorId,
                                                                               @RequestParam int size) {
        return ResponseDto.ok(commentService.guestReadOldestReply(commentId, cursorId, size));
    }
}
