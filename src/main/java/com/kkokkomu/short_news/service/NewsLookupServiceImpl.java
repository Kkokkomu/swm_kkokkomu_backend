package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsLookupServiceImpl implements NewsLookupService{
    private final NewsRepository newsRepository;

    @Override
    public News findNewsById(Long newsId) {
        return newsRepository.findById(newsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));
    }

    @Override
    public Boolean existNewsById(Long newsId) {
        return newsRepository.existsById(newsId);
    }
}
