package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.news.domain.News;
import org.springframework.stereotype.Service;

@Service
public interface NewsLookupService {
    News findNewsById(Long newsId);

    Boolean existNewsById(Long newsId);

    void deleteNewsById(Long newsId);
}
