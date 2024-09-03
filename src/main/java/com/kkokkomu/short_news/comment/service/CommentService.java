package com.kkokkomu.short_news.comment.service;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.comment.dto.comment.response.*;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.service.NewsLookupService;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.comment.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.comment.dto.comment.request.CreateReplyDto;
import com.kkokkomu.short_news.comment.dto.comment.request.UpdateCommentDto;
import com.kkokkomu.short_news.comment.dto.comment.request.UpdateReplyDto;
import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.user.dto.user.response.CommentSummoryDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.comment.repository.CommentRepository;
import com.kkokkomu.short_news.user.service.UserLookupService;
import com.kkokkomu.short_news.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.kkokkomu.short_news.core.constant.Constant.LIKE_WEIGHT;
import static com.kkokkomu.short_news.core.constant.Constant.REPLY_WEIGHT;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;

    private final UserLookupService userLookupService;
    private final CommentLikeService commentLikeService;
    private final NewsLookupService newsLookupService;

    /* 댓글 */

    public CommentDto createComment(Long userId, CreateCommentDto createCommentDto) {
        log.info("createComment service");
        User user = userLookupService.findUserById(userId);

        News news = newsLookupService.findNewsById(createCommentDto.newsId());

        Comment comment = commentRepository.save(
                Comment.builder()
                        .user(user)
                        .news(news)
                        .content(createCommentDto.content())
                        .parent(null)
                        .build()
        );

        return CommentDto.builder()
                .id(comment.getId())
                .newsId(createCommentDto.newsId())
                .content(comment.getContent())
                .userId(userId)
                .editedAt(comment.getEditedAt().toString())
                .build();
    } // 댓글 생성

    public String deleteComment(Long commentId) {
        log.info("deleteComment service");
        commentRepository.findById(commentId)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));

        commentRepository.deleteById(commentId);
        return "success";
    } // 댓글 삭제

    public String updateComment(UpdateCommentDto updateCommentDto) {
        log.info("updateComment service");
        Comment comment = commentRepository.findById(updateCommentDto.commentId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));

        comment.update(updateCommentDto.content());

        commentRepository.save(comment);

        return updateCommentDto.content();
    } // 댓글 수정

    @Transactional
    public CursorResponseDto<List<CommentListDto>> readLatestComments(Long userId, Long newsId, Long cursorId, int size) {
        log.info("readLatestComments service");
        User user = userLookupService.findUserById(userId);
        // 요청한 뉴스랑 댓글이 존재하는지 검사
        if (!newsLookupService.existNewsById(newsId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_NEWS);
        }
        if (cursorId != null && !commentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        // size에 따른 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(0, size);

        // 댓글 조회
        List<Comment> comments;
        Page<Comment> results;
        if (cursorId == null) {
            // 처음 요청
            results = commentRepository.findFirstPageByNewsIdOrderByIdDesc(newsId, user, pageRequest);
            comments = results.getContent();
        } else {
            // 2번째부터
            results = commentRepository.findByNewsIdAndIdLessThanOrderByIdDesc(newsId, cursorId, user, pageRequest);
            comments = results.getContent();
        }

        // 유저가 좋아요한 댓글들 불러오기
        List<Comment> commentList = commentRepository.findByUserAndCommentLike(user);

        List<CommentListDto> commentListDtos = new ArrayList<>();
        Boolean userLike = null;
        for (Comment comment : comments) {
            if (commentList == null) {
                userLike = false;
            } else {
                userLike = commentList.contains(comment);
            }

            commentListDtos.add(
                    CommentListDto.builder()
                            .commentLikeCnt(commentLikeService.countByComment(comment))
                            .replyCnt(comment.getChildren().size())
                            .user(CommentSummoryDto.of(comment.getUser()))
                            .comment(CommentDto.of(comment))
                            .userLike(userLike)
                            .build()
            );
        }

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(commentListDtos, cursorInfoDto);
    } // 최신순 댓글 조회

    @Transactional
    public CursorResponseDto<List<GuestCommentListDto>> guestReadLatestComments(Long newsId, Long cursorId, int size) {
        log.info("geustReadLatestComments service");
        // 요청한 뉴스랑 댓글이 존재하는지 검사
        if (!newsLookupService.existNewsById(newsId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_NEWS);
        }
        if (cursorId != null && !commentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        // size에 따른 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(0, size);

        // 댓글 조회
        List<Comment> comments;
        Page<Comment> results;
        if (cursorId == null) {
            // 처음 요청
            results = commentRepository.findFirstPageByNewsIdOrderByIdDescGuest(newsId, pageRequest);
            comments = results.getContent();
        } else {
            // 2번째부터
            results = commentRepository.findByNewsIdAndIdLessThanOrderByIdDescGuest(newsId, cursorId, pageRequest);
            comments = results.getContent();
        }

        List<GuestCommentListDto> commentListDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentListDtos.add(
                    GuestCommentListDto.builder()
                            .commentLikeCnt(commentLikeService.countByComment(comment))
                            .replyCnt(comment.getChildren().size())
                            .user(CommentSummoryDto.of(comment.getUser()))
                            .comment(CommentDto.of(comment))
                            .build()
            );
        }

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(commentListDtos, cursorInfoDto);
    } // 비로그인 최신순 댓글 조회

    @Transactional
    public CursorResponseDto<List<CommentListDto>> readPopularComments(Long userId, Long newsId, Long cursorId, int size) {
        log.info("readPopularComments service");

        User user = userLookupService.findUserById(userId);

        // 요청한 뉴스랑 댓글이 존재하는지 검사
        if (!newsLookupService.existNewsById(newsId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_NEWS);
        }
        if (cursorId != null && !commentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        // 댓글 조회
        List<Comment> comments;
        Page<Comment> results;
        if (cursorId == null) {
             results = commentRepository.findFirstPageByNewsIdAndPopularity(newsId, REPLY_WEIGHT, LIKE_WEIGHT, user, pageRequest);
             comments = results.getContent();
        } else {
            // 커서 댓글 찾기
            Comment cursorComment = commentRepository.findById(cursorId)
                    .orElseThrow(() -> new CommonException(ErrorCode.INVALID_COMMENT_CURSOR));

            // 커서 점수 계산
            double cursorScore = (cursorComment.getChildren().size() * REPLY_WEIGHT) +
                    (cursorComment.getLikes().size() * LIKE_WEIGHT);

            results = commentRepository.findByNewsIdAndPopularityLessThan(newsId, REPLY_WEIGHT, LIKE_WEIGHT, cursorScore, cursorId, user, pageRequest);
            comments = results.getContent();
        }

        // 유저가 좋아요한 댓글들 불러오기
        List<Comment> commentList = commentRepository.findByUserAndCommentLike(user);

        List<CommentListDto> commentListDtos = new ArrayList<>();
        Boolean userLike = null;
        for (Comment comment : comments) {
            if (commentList == null) {
                userLike = false;
            } else {
                userLike = commentList.contains(comment);
            }

            commentListDtos.add(
                    CommentListDto.builder()
                            .commentLikeCnt(commentLikeService.countByComment(comment))
                            .replyCnt(comment.getChildren().size())
                            .user(CommentSummoryDto.of(comment.getUser()))
                            .comment(CommentDto.of(comment))
                            .userLike(userLike)
                            .build()
            );
        }

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(commentListDtos, cursorInfoDto);
    } // 인기순 댓글 조회

    @Transactional
    public CursorResponseDto<List<GuestCommentListDto>> guestReadPopularComments(Long newsId, Long cursorId, int size) {
        log.info("guestReadPopularComments service");

        // 요청한 뉴스랑 댓글이 존재하는지 검사
        if (!newsLookupService.existNewsById(newsId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_NEWS);
        }
        if (cursorId != null && !commentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        // 댓글 조회
        List<Comment> comments;
        Page<Comment> results;
        if (cursorId == null) {
             results = commentRepository.findFirstPageByNewsIdAndPopularityGuest(newsId, REPLY_WEIGHT, LIKE_WEIGHT, pageRequest);
             comments = results.getContent();
        } else {
            // 커서 댓글 찾기
            Comment cursorComment = commentRepository.findById(cursorId)
                    .orElseThrow(() -> new CommonException(ErrorCode.INVALID_COMMENT_CURSOR));

            // 커서 점수 계산
            Long cursorScore = (cursorComment.getChildren().size() * REPLY_WEIGHT) +
                    (cursorComment.getLikes().size() * LIKE_WEIGHT);

            results = commentRepository.findByNewsIdAndPopularityLessThanGuest(newsId, REPLY_WEIGHT, LIKE_WEIGHT, cursorScore, cursorId, pageRequest);
            comments = results.getContent();
        }

        List<GuestCommentListDto> commentListDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentListDtos.add(
                    GuestCommentListDto.builder()
                            .commentLikeCnt(commentLikeService.countByComment(comment))
                            .replyCnt(comment.getChildren().size())
                            .user(CommentSummoryDto.of(comment.getUser()))
                            .comment(CommentDto.of(comment))
                            .build()
            );
        }

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(commentListDtos, cursorInfoDto);
    } // 비로그인 인기순 댓글 조회

    /* 대댓글 */

    public ReplyDto createReply(Long userId, CreateReplyDto createReplyDto) {
        log.info("createReply service");
        User user = userLookupService.findUserById(userId);

        News news = newsLookupService.findNewsById(createReplyDto.newsId());

        Comment parent = commentRepository.findById(createReplyDto.commentId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PARENT_COMMENT));

        Comment reply = commentRepository.save(
                Comment.builder()
                        .user(user)
                        .news(news)
                        .content(createReplyDto.content())
                        .parent(parent)
                        .build()
        );

        return ReplyDto.builder()
                .id(reply.getId())
                .userId(userId)
                .newsId(createReplyDto.newsId())
                .parentId(parent.getId())
                .content(reply.getContent())
                .editedAt(reply.getEditedAt().toString())
                .build();
    } // 대댓글 생성

    public String deleteReply(Long replyId) {
        log.info("deleteReply service");
        commentRepository.findById(replyId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REPLY));

        commentRepository.deleteById(replyId);
        return "success";
    } // 대댓글 삭제

    public String updateReply(UpdateReplyDto updateReplyDto) {
        log.info("updateComment");
        Comment reply = commentRepository.findById(updateReplyDto.replyId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REPLY));

        reply.update(updateReplyDto.content());

        commentRepository.save(reply);

        return updateReplyDto.content();
    } // 댓글 수정

    @Transactional
    public CursorResponseDto<List<ReplyListDto>> readOldestReply(Long userId, Long parentId, Long cursorId, int size) {
        User user = userLookupService.findUserById(userId);

        // 요청한 대댓글의 부모가 존재하는지
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CURSOR));

        // 요청한 커서가 존재하는지
        if (cursorId != null && !commentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_COMMENT);
        }

        // size에 따른 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(0, size);

        // 댓글 조회
        List<Comment> replies;
        Page<Comment> results;
        if (cursorId == null) {
            // 처음 요청
            results = commentRepository.findFirstPageByParentOrderById(parent, user, pageRequest);
            replies = results.getContent();
        } else {
            // 2번째부터
            results = commentRepository.findByParentAndIdLessThanOrderById(parent, cursorId, user, pageRequest);
            replies = results.getContent();
        }

        // 유저가 좋아요한 댓글들 불러오기
        List<Comment> commentList = commentRepository.findByUserAndCommentLike(user);

        List<ReplyListDto> replyListDtos = new ArrayList<>();
        Boolean userLike = null;
        for (Comment reply : replies) {
            if (commentList == null) {
                userLike = false;
            } else {
                userLike = commentList.contains(reply);
            }

            replyListDtos.add(
                    ReplyListDto.builder()
                            .commentLikeCnt(commentLikeService.countByComment(reply))
                            .user(CommentSummoryDto.of(reply.getUser()))
                            .comment(CommentDto.of(reply))
                            .build()
            );
        }

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(replyListDtos, cursorInfoDto);
    } // 오래된순 대댓글 조회

    @Transactional
    public CursorResponseDto<List<ReplyListDto>> guestReadOldestReply(Long parentId, Long cursorId, int size) {
        // 요청한 대댓글의 부모가 존재하는지
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CURSOR));

        // 요청한 커서가 존재하는지
        if (cursorId != null && !commentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_COMMENT);
        }

        // size에 따른 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(0, size);

        // 댓글 조회
        List<Comment> replies;
        Page<Comment> results;
        if (cursorId == null) {
            // 처음 요청
            results = commentRepository.findFirstPageByParentOrderByIdGuest(parent, pageRequest);
            replies = results.getContent();
        } else {
            // 2번째부터
            results = commentRepository.findByParentAndIdLessThanOrderByIdGuest(parent, cursorId, pageRequest);
            replies = results.getContent();
        }

        List<ReplyListDto> replyListDtos = new ArrayList<>();
        for (Comment reply : replies) {
            replyListDtos.add(
                    ReplyListDto.builder()
                            .commentLikeCnt(commentLikeService.countByComment(reply))
                            .user(CommentSummoryDto.of(reply.getUser()))
                            .comment(CommentDto.of(reply))
                            .build()
            );
        }

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(replyListDtos, cursorInfoDto);
    } // 비로그인 오래된순 대댓글 조회
}
