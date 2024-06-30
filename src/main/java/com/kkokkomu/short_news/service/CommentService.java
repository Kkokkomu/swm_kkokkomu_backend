package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.dto.comment.request.UpdateCommentDto;
import com.kkokkomu.short_news.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.dto.comment.response.CommentWithUserDto;
import com.kkokkomu.short_news.dto.common.PageInfoDto;
import com.kkokkomu.short_news.dto.common.PagingResponseDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.CommentLikeRepository;
import com.kkokkomu.short_news.repository.CommentRepository;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentDto saveComment(CreateCommentDto createCommentDto) {
        log.info("saveComment");
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
    } // 댓글 작성

    public PagingResponseDto readCommentList(Long newsId, int page, int size) {
        log.info("readCommentList");
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Page<Comment> commentsPage = commentRepository.findAllByNews(news, PageRequest.of(page, size, sort));
        List<Comment> comments = commentsPage.getContent();

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(commentsPage);

        List<CommentWithUserDto> commentWithUserDtos = new ArrayList<>();
        for (Comment comment : comments) {
            Long commentCnt = commentLikeRepository.countByComment(comment);

            CommentWithUserDto commentWithUserDto = CommentWithUserDto.builder()
                    .commentId(comment.getId())
                    .userProfileImg("https://media.istockphoto.com/id/1885866215/ko/%EC%82%AC%EC%A7%84/veterinarian-examines-the-pet.jpg?s=1024x1024&w=is&k=20&c=zvI2Xt-IWFbI_7YsDANNZ9x31cvtOn_K6LTDreoT4EE=")
                    .userId(comment.getUser().getId())
                    .createdAt(comment.getCreatedAt().toString())
                    .comment(comment.getContent())
                    .great(commentCnt)
                    .build();
            commentWithUserDtos.add(commentWithUserDto);
        }

        return PagingResponseDto.fromEntityAndPageInfo(commentWithUserDtos, pageInfoDto);
    } // 댓글 리스트 조회

    public CommentDto updateComment(UpdateCommentDto updateCommentDto) {
        log.info("updateComment");
        Comment comment = commentRepository.findById(updateCommentDto.commentId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));

        comment.updateComment(updateCommentDto.comment());

        comment = commentRepository.save(comment);

        return CommentDto.fromEntity(comment);
    }// 댓글 수정

    public String deleteComment(Long commentId) {
        log.info("deleteComment");
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));
        commentRepository.delete(comment);
        return "success";
    }
}
