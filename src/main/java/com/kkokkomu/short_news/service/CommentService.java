package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.dto.comment.request.CreateReplyDto;
import com.kkokkomu.short_news.dto.comment.request.UpdateCommentDto;
import com.kkokkomu.short_news.dto.comment.response.*;
import com.kkokkomu.short_news.dto.user.response.CommentSummoryDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.CommentLikeRepository;
import com.kkokkomu.short_news.repository.CommentRepository;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.kkokkomu.short_news.constant.Constant.LIKE_WEIGHT;
import static com.kkokkomu.short_news.constant.Constant.REPLY_WEIGHT;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;
    private final CommentLikeRepository commentLikeRepository;

    /* 댓글 */

    public CommentDto createComment(Long userId, CreateCommentDto createCommentDto) {
        log.info("createComment service");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        News news = newsRepository.findById(createCommentDto.newsId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));

        Comment comment = commentRepository.save(
                Comment.builder()
                        .user(user)
                        .news(news)
                        .content(createCommentDto.content())
                        .parent(null)
                        .build()
        );

        return CommentDto.builder()
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
    public List<CommentListDto> readLatestComments(Long newsId, Long cursorId, int size) {
        log.info("readLatestComments service");
        // 요청한 뉴스랑 댓글이 존재하는지 검사
        if (!newsRepository.existsById(newsId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_NEWS);
        }
        if (cursorId != null && !commentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_COMMENT);
        }

        // size에 따른 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(0, size);

        // 댓글 조회
        List<Comment> comments;
        if (cursorId == null) {
            // 처음 요청
            comments = commentRepository.findFirstPageByNewsIdOrderByIdDesc(newsId, pageRequest);
        } else {
            // 2번째부터
            comments = commentRepository.findByNewsIdAndIdLessThanOrderByIdDesc(newsId, cursorId, pageRequest);
        }

        List<CommentListDto> commentListDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentListDtos.add(
                    CommentListDto.builder()
                            .commentLikeCnt(commentLikeRepository.countByComment(comment))
                            .replyCnt(comment.getChildren().size())
                            .user(CommentSummoryDto.of(comment.getUser()))
                            .comment(CommentDto.of(comment))
                            .build()
            );
        }

        return commentListDtos;
    } // 최신순 댓글 조회

    @Transactional
    public List<CommentListDto> readPopularComments(Long newsId, Long cursorId, int size) {
        log.info("readPopularComments service");

        // 요청한 뉴스랑 댓글이 존재하는지 검사
        if (!newsRepository.existsById(newsId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_NEWS);
        }
        if (cursorId != null && !commentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_COMMENT);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        // 댓글 조회
        List<Comment> comments;
        if (cursorId == null) {
             comments = commentRepository.findFirstPageByNewsIdAndPopularity(newsId, REPLY_WEIGHT, LIKE_WEIGHT, pageRequest);
        } else {
            // 커서 댓글 찾기
            Comment cursorComment = commentRepository.findById(cursorId)
                    .orElseThrow(() -> new CommonException(ErrorCode.INVALID_COMMENT_CURSOR));

            // 커서 점수 계산
            double cursorScore = (cursorComment.getChildren().size() * REPLY_WEIGHT) +
                    (cursorComment.getLikes().size() * LIKE_WEIGHT);

            comments = commentRepository.findByNewsIdAndPopularityLessThan(newsId, REPLY_WEIGHT, LIKE_WEIGHT, cursorScore, cursorId, pageRequest);
        }

        List<CommentListDto> commentListDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentListDtos.add(
                    CommentListDto.builder()
                            .commentLikeCnt(commentLikeRepository.countByComment(comment))
                            .replyCnt(comment.getChildren().size())
                            .user(CommentSummoryDto.of(comment.getUser()))
                            .comment(CommentDto.of(comment))
                            .build()
            );
        }

        return commentListDtos;
    } // 인기순 댓글 조회

    /* 대댓글 */

    public ReplyDto createReply(Long userId, CreateReplyDto createReplyDto) {
        log.info("createReply service");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        News news = newsRepository.findById(createReplyDto.newsId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));

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

//    public String updateComment(UpdateCommentDto updateCommentDto) {
//        log.info("updateComment");
//        Comment comment = commentRepository.findById(updateCommentDto.commentId())
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));
//
//        comment.update(updateCommentDto.content());
//
//        commentRepository.save(comment);
//
//        return updateCommentDto.content();
//    } // 댓글 수정

    @Transactional
    public ReplyByParentDto readOldestReply(Long parentId, Long cursorId, int size) {
        // 요청한 대댓글의 부모가 존재하는지
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));

        // 요청한 커서가 존재하는지
        if (cursorId != null && !commentRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_COMMENT);
        }

        // size에 따른 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(0, size);

        // 댓글 조회
        List<Comment> replies;
        if (cursorId == null) {
            // 처음 요청
            replies = commentRepository.findFirstPageByParentOrderById(parent, pageRequest);
        } else {
            // 2번째부터
            replies = commentRepository.findByParentAndIdLessThanOrderById(parent, cursorId, pageRequest);
        }

        List<ReplyListDto> replyListDtos = new ArrayList<>();
        for (Comment reply : replies) {
            replyListDtos.add(
                    ReplyListDto.builder()
                            .commentLikeCnt(commentLikeRepository.countByComment(reply))
                            .user(CommentSummoryDto.of(reply.getUser()))
                            .comment(CommentDto.of(reply))
                            .build()
            );
        }

        CommentListDto commentListDto = CommentListDto.builder()
                .commentLikeCnt(commentLikeRepository.countByComment(parent))
                .replyCnt(parent.getChildren().size())
                .user(CommentSummoryDto.of(parent.getUser()))
                .comment(CommentDto.of(parent))
                .build();

        return ReplyByParentDto.builder()
                .parentComment(commentListDto)
                .replies(replyListDtos)
                .build();
    } // 오래된순 대댓글 조회
}
