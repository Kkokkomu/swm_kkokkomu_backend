package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsLookupServiceImpl implements NewsLookupService{
    private static final Logger log = LoggerFactory.getLogger(NewsLookupServiceImpl.class);
    private final NewsRepository newsRepository;

    @Override
    public News findNewsById(Long newsId) {
        log.info("newsId={}", newsId);
        return newsRepository.findById(newsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));
    }

    @Override
    public Boolean existNewsById(Long newsId) {
        return newsRepository.existsById(newsId);
    }

    @Override
    public void deleteNewsById(Long newsId) {
        newsRepository.deleteById(newsId);
    }
}
