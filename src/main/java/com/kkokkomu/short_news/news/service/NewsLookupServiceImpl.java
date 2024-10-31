package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.core.config.service.RedisService;
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

    private final RedisService redisService;

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
    public String deleteNewsById(Long newsId) {
        News news = findNewsById(newsId);

        newsRepository.delete(news);

        // 레디스에서 뉴스 삭제
        redisService.deleteAllNewsData(newsId);

        return "success";
    }
}
