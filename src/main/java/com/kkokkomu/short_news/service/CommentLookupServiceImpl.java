package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.CommentRepository;
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
}
