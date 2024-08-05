package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.dto.comment.request.UpdateCommentDto;
import com.kkokkomu.short_news.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.CommentRepository;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    public CommentDto createComment(Long userId, CreateCommentDto createCommentDto) {
        log.info("addComment");
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
                .build();
    } // 댓글 생성

    public String deleteComment(Long commentId) {
        log.info("deleteComment");
        commentRepository.findById(commentId)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));

        commentRepository.deleteById(commentId);
        return "success";
    } // 댓글 삭제

    public String updateComment(UpdateCommentDto updateCommentDto) {
        log.info("updateComment");
        Comment comment = commentRepository.findById(updateCommentDto.commentId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));

        comment.update(updateCommentDto.content());

        commentRepository.save(comment);

        return updateCommentDto.content();
    } // 댓글 수정
}
