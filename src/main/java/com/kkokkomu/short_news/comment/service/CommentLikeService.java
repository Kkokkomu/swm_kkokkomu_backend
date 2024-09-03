package com.kkokkomu.short_news.comment.service;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.comment.domain.CommentLike;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.comment.dto.commentLike.request.CreateCommentLike;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.comment.repository.CommentLikeRepository;
import com.kkokkomu.short_news.user.service.UserLookupService;
import com.kkokkomu.short_news.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    private final UserLookupService userLookupService;
    private final CommentLookupService commentLookupService;

    public String createCommentLike(Long userId, CreateCommentLike createCommentLike) {
        log.info("createCommentLike");
        User user = userLookupService.findUserById(userId);

        Comment comment = commentLookupService.findCommentById(createCommentLike.commentId());

        isDuplicateCommentLike(user, comment);

        commentLikeRepository.save(
                CommentLike.builder()
                        .user(user)
                        .comment(comment)
                        .build()
        );

        return "success";
    } // 댓글 좋아요 생성

    @Transactional
    public String deleteCommentLike(Long userId, Long commentId) {
        log.debug("deleteCommentLike");
        User user = userLookupService.findUserById(userId);

        Comment comment = commentLookupService.findCommentById(commentId);

        existsCommentLike(user, comment);

        commentLikeRepository.deleteByCommentAndUser(comment, user);

        return "success";
    } // 댓글 좋아요 삭제

    public void existsCommentLike(User user, Comment comment) {
        if (!commentLikeRepository.existsByCommentAndUser(comment, user)) {
            throw new CommonException(ErrorCode.NOT_FOUND_COMMENT_LIKE);
        }
    } // 댓글 좋아요 객체가 존재하는지 검사

    public void isDuplicateCommentLike(User user, Comment comment) {
        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            throw new CommonException(ErrorCode.DUPLICATED_COMMENT_LIKE);
        }
    } // 댓글 좋아요 객체가 존재하는지 검사

    public Long countByComment(Comment comment) {
        return commentLikeRepository.countByComment(comment);
    }
}
