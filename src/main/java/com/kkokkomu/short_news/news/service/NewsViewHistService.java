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
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateNewsHist(Long userId) {
        Set<Long> newsIds = redisService.getNewsViewHistory(userId);
        log.info(newsIds.size() + String.valueOf(newsIds.isEmpty()) + " new news history");
        if (newsIds != null && !newsIds.isEmpty()) {
            User user = userLookupService.findUserById(userId);
            for (Object newsIdObj : newsIds) {
                Long newsId = Long.valueOf(newsIdObj.toString());

                // 이미 저장된 기록이 없다면 저장
                if (!newsViewHistRepository.existsByUserIdAndNewsId(userId, newsId)) {
                    News news = newsLookupService.findNewsById(newsId);
                    NewsViewHist newsViewHist = NewsViewHist.builder()
                            .news(news)
                            .user(user)
                            .build();
                    newsViewHistRepository.save(newsViewHist);
                }
            }

            // Redis에서 시청 기록 삭제
            redisService.deleteNewsAllViewHistory(userId);
        }
    } // 레디스로 부터 해당 유저의 뉴스 시청기록 디비에 동기화
}
