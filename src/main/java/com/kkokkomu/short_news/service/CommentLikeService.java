package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.CommentLike;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.commentLike.request.CreateCommentLikeDto;
import com.kkokkomu.short_news.dto.commentLike.response.CommentLikeDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.CommentLikeRepository;
import com.kkokkomu.short_news.repository.CommentRepository;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentLikeDto addCommentLike(CreateCommentLikeDto createCommentLikeDto) {
        log.info("service: addCommentLike");
        User user = userRepository.findByUuid(createCommentLikeDto.userId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Comment comment = commentRepository.findById(createCommentLikeDto.commentId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));

        // 이미 등록된 댓글 좋아요가 있다면 에러 처리
        if (commentLikeRepository.existsByCommentAndUser(comment, user)){
            throw new CommonException(ErrorCode.DUPLICATED_COMMENT_LIKE);
        }

        CommentLike commentLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();

        commentLikeRepository.save(commentLike);

        return CommentLikeDto.fromEntity(commentLike);
    } // 댓글 좋아요 추가

//    public String deleteCommentLike(CreateCommentLikeDto createCommentLikeDto) {
//        log.info("service: deleteCommentLike");
//        User user = userRepository.findByUuid(createCommentLikeDto.userId())
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
//        Comment comment = commentRepository.findById(createCommentLikeDto.commentId())
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));
//
//        CommentLike commentLike = commentLikeRepository.findByCommentAndUser(comment, user)
//    }
}
