package com.kkokkomu.short_news.comment.service;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLookupServiceImpl implements CommentLookupService {
    private final CommentRepository commentRepository;

    @Override
    public Comment findCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CURSOR));
    }

    @Override
    public void deleteCommentById(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new CommonException(ErrorCode.NOT_FOUND_COMMENT);
        }

        commentRepository.deleteById(id);
    }

    @Override
    public Boolean existsCommentById(Long id) {
        return commentRepository.existsById(id);
    }
}
