package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Comment;
import org.springframework.stereotype.Service;

@Service
public interface CommentLookupService {
    Comment findCommentById(Long id);
}
