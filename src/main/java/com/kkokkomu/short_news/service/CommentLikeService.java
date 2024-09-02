package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.CommentLike;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.commentLike.request.CreateCommentLike;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.CommentLikeRepository;
import com.kkokkomu.short_news.repository.CommentRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    private final UserService userService;
    private final CommentLookupService commentLookupService;

    public String createCommentLike(Long userId, CreateCommentLike createCommentLike) {
        log.info("createCommentLike");
        User user = userService.findUserById(userId);

        Comment comment = commentLookupService.findCommentById(createCommentLike.commentId());

        existsCommentLike(user, comment);

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
        User user = userService.findUserById(userId);

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

    public Long countByComment(Comment comment) {
        return commentLikeRepository.countByComment(comment);
    }
}
