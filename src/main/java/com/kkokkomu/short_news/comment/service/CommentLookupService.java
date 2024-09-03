package com.kkokkomu.short_news.comment.service;

import com.kkokkomu.short_news.comment.domain.Comment;
import org.springframework.stereotype.Service;

@Service
public interface CommentLookupService {
    Comment findCommentById(Long id);
}
