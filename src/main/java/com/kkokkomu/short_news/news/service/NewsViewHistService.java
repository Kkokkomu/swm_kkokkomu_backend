package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.core.config.service.RedisService;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.domain.NewsViewHist;
import com.kkokkomu.short_news.news.repository.NewsViewHistRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsViewHistService {
    private final NewsViewHistRepository newsViewHistRepository;

    private final RedisService redisService;
    private final NewsLookupService newsLookupService;
    private final UserLookupService userLookupService;

    @Transactional
    public void deleteAllByUser(User user) {
        log.info("NewsViewHistService deleteAllByUser start");
        newsViewHistRepository.deleteAllByUser(user);
    }

    @Transactional
    public void updateNewsHist(Long userId) {
        Set<Long> newsIds = redisService.getNewsViewHistory(userId);
        if (newsIds != null && !newsIds.isEmpty()) {
            User user = userLookupService.findUserById(userId);
            for (Object newsIdObj : newsIds) {
                Long newsId = Long.valueOf(newsIdObj.toString());
                News news = newsLookupService.findNewsById(newsId);
                NewsViewHist newsViewHist = new NewsViewHist(user, news, LocalDateTime.now());
                newsViewHistRepository.save(newsViewHist);
            }

            // Redis에서 시청 기록 삭제
            redisService.deleteNewsViewHistory(userId);
        }
    }
}
