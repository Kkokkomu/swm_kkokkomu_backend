package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.CommentRepository;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    public CommentDto saveComment(CreateCommentDto createCommentDto) {
        User user = userRepository.findByUuid(createCommentDto.userId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        News news = newsRepository.findById(createCommentDto.newsId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));

        Comment comment = Comment.builder()
                .user(user)
                .news(news)
                .content(createCommentDto.comment())
                .build();

        commentRepository.save(comment);

        return CommentDto.fromEntity(comment);
    }
}
