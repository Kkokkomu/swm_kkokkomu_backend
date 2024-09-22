package com.kkokkomu.short_news.core.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kkokkomu.short_news.core.constant.Constant.*;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /****** 뉴스 랭킹 ******/

    private static final String NEWS_RANKING_KEY = "news:ranking";

    // 뉴스 조회수 증가 및 랭킹 업데이트
    public void incrementRankingByView(Long newsId) {
        String key = NEWS_VIEW_COUNT_PREFIX + newsId;
        redisTemplate.opsForValue().increment(key);
        increaseScore(newsId, VIEW_WEIGHT);
    }

    // 랭킹 점수 증가 메소드
    private void increaseScore(Long newsId, Long weight) {
        redisTemplate.opsForZSet().incrementScore(NEWS_RANKING_KEY, String.valueOf(newsId), weight);
    }

    // 랭킹 점수 감소 메소드
    private void decreaseScore(Long newsId, Long weight) {
        redisTemplate.opsForZSet().incrementScore(NEWS_RANKING_KEY, String.valueOf(newsId), -weight);
    }

    // 뉴스 랭킹 조회
    public Set<String> getTopNews(int topN) {
        return redisTemplate.opsForZSet().reverseRange(NEWS_RANKING_KEY, 0, topN - 1);
    }

    // 댓글 수 증가 시 랭킹 업데이트
    public void incrementRankingByComment(Long newsId) {
        increaseScore(newsId, COMMENT_WEIGHT);
    }

    // 댓글 수 감소 시 랭킹 업데이트
    public void decreaseRankingByComment(Long newsId) {
        decreaseScore(newsId, COMMENT_WEIGHT);
    }

    // 반응 수 증가 시 랭킹 업데이트
    public void incrementRankingByReaction(Long newsId) {
        increaseScore(newsId, REACTION_WEIGHT);
    }

    // 반응 수 감소 시 랭킹 업데이트
    public void decreaseRankingByReaction(Long newsId) {
        decreaseScore(newsId, REACTION_WEIGHT);
    }

    // 공유 수 증가 시 랭킹 업데이트
    public void incrementRankingByShare(Long newsId) {
        increaseScore(newsId, SHARE_WEIGHT);
    }

    // 최상위 뉴스 점수 감소 로직
    public void normalizeScores() {
        Set<ZSetOperations.TypedTuple<String>> topNews = redisTemplate.opsForZSet().reverseRangeWithScores(NEWS_RANKING_KEY, 0, 0);
        if (topNews != null && !topNews.isEmpty()) {
            Double topScore = topNews.stream().findFirst().get().getScore();
            if (topScore != null) {
                redisTemplate.opsForZSet().rangeWithScores(NEWS_RANKING_KEY, 0, -1).forEach(news -> {
                    Double currentScore = news.getScore();
                    Long newsId = Long.valueOf(Objects.requireNonNull(news.getValue()));
                    // 현재 점수에서 최상위 점수를 빼기
                    redisTemplate.opsForZSet().add(NEWS_RANKING_KEY, String.valueOf(newsId), currentScore - topScore);
                });
            }
        }
    }

    /****** 뉴스 조회수 ******/

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

    public void resetViewCount(Long newsId) {
        String key = NEWS_VIEW_COUNT_PREFIX + newsId;
        redisTemplate.opsForValue().set(key, "0");
    }

    /****** 뉴스 시청기록 ******/

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

