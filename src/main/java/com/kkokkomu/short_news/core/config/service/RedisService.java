package com.kkokkomu.short_news.core.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 뉴스 조회수
    private static final String NEWS_VIEW_COUNT_PREFIX = "news:viewCount:";

    public void incrementViewCount(Long newsId) {
        String key = NEWS_VIEW_COUNT_PREFIX + newsId;
        redisTemplate.opsForValue().increment(key);
    }

    public Integer getViewCount(Long newsId) {
        String key = NEWS_VIEW_COUNT_PREFIX + newsId;
        String count = redisTemplate.opsForValue().get(key);
        return count != null ? Integer.parseInt(count) : 0;
    }

    // 뉴스 시청기록
    private static final String VIEW_HISTORY_PREFIX = "news:viewHistory:";

    public void saveNewsViewHistory(Long userId, Long newsId) {
        String key = VIEW_HISTORY_PREFIX + userId;
        redisTemplate.opsForSet().add(key, String.valueOf(newsId));
    }

    public Set<Long> getNewsViewHistory(Long userId) {
        String key = VIEW_HISTORY_PREFIX + userId;
        Set<String> stringSet = redisTemplate.opsForSet().members(key);  // Set<String> 반환

        // Set<String>을 Set<Long>으로 변환
        return stringSet.stream()
                .map(Long::valueOf)  // String -> Long 변환
                .collect(Collectors.toSet());
    }

    public void deleteNewsViewHistory(Long userId) {
        String key = VIEW_HISTORY_PREFIX + userId;
        redisTemplate.delete(key);
    }
}

