package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.comment.request.CreateCommentDto;
import com.kkokkomu.short_news.dto.comment.request.UpdateCommentDto;
import com.kkokkomu.short_news.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.dto.comment.response.CommentListDto;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;
    private final CommentLikeRepository commentLikeRepository;

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
                .editedAt(comment.getEditedAt().toString())
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

    @Transactional
    public List<CommentListDto> readLatestComments(Long newsId, Long cursorId, int size) {
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
    }
}
