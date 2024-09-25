package com.kkokkomu.short_news.core.config.service;

import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.news.domain.News;
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

    // 뉴스 랭킹 키 패턴
    private static final String NEWS_RANKING_KEY = "news:ranking:%s";
    private static final String GLOBAL_RANKING_KEY = "news:ranking:global";

    // 뉴스 조회수 증가 및 랭킹 업데이트
    public void incrementRankingByView(News news) {
        String categoryKey = String.format(NEWS_RANKING_KEY, news.getCategory().name().toLowerCase());
        String globalKey = GLOBAL_RANKING_KEY;
        String viewKey = NEWS_VIEW_COUNT_PREFIX + news.getId();
        redisTemplate.opsForValue().increment(viewKey);
        increaseScore(news.getId(), VIEW_WEIGHT, categoryKey);
        increaseScore(news.getId(), VIEW_WEIGHT, globalKey);
    }

    // 랭킹 점수 증가 메소드
    private void increaseScore(Long newsId, Long weight, String rankingKey) {
        redisTemplate.opsForZSet().incrementScore(rankingKey, String.valueOf(newsId), weight);
    }

    // 랭킹 점수 감소 메소드
    private void decreaseScore(Long newsId, Long weight, String rankingKey) {
        redisTemplate.opsForZSet().incrementScore(rankingKey, String.valueOf(newsId), -weight);
    }

    // 카테고리별 뉴스 랭킹 조회
    public Set<String> getTopNews(int topN, ECategory category) {
        String key = String.format(NEWS_RANKING_KEY, category.name().toLowerCase());
        return redisTemplate.opsForZSet().reverseRange(key, 0, topN - 1);
    }

    // 전체 뉴스 랭킹 조회
    public Set<String> getTopGlobalNews(int topN) {
        String key = GLOBAL_RANKING_KEY;
        return redisTemplate.opsForZSet().reverseRange(key, 0, topN - 1);
    }

    // 댓글 수 증가 시 랭킹 업데이트
    public void incrementRankingByComment(News news) {
        String categoryKey = String.format(NEWS_RANKING_KEY, news.getCategory().name().toLowerCase());
        String globalKey = GLOBAL_RANKING_KEY;
        increaseScore(news.getId(), COMMENT_WEIGHT, categoryKey);
        increaseScore(news.getId(), COMMENT_WEIGHT, globalKey);
    }

    // 댓글 수 감소 시 랭킹 업데이트
    public void decreaseRankingByComment(News news) {
        String categoryKey = String.format(NEWS_RANKING_KEY, news.getCategory().name().toLowerCase());
        String globalKey = GLOBAL_RANKING_KEY;
        decreaseScore(news.getId(), COMMENT_WEIGHT, categoryKey);
        decreaseScore(news.getId(), COMMENT_WEIGHT, globalKey);
    }

    // 반응 수 증가 시 랭킹 업데이트
    public void incrementRankingByReaction(News news) {
        String categoryKey = String.format(NEWS_RANKING_KEY, news.getCategory().name().toLowerCase());
        String globalKey = GLOBAL_RANKING_KEY;
        increaseScore(news.getId(), REACTION_WEIGHT, categoryKey);
        increaseScore(news.getId(), REACTION_WEIGHT, globalKey);
    }

    // 반응 수 감소 시 랭킹 업데이트
    public void decreaseRankingByReaction(News news) {
        String categoryKey = String.format(NEWS_RANKING_KEY, news.getCategory().name().toLowerCase());
        String globalKey = GLOBAL_RANKING_KEY;
        decreaseScore(news.getId(), REACTION_WEIGHT, categoryKey);
        decreaseScore(news.getId(), REACTION_WEIGHT, globalKey);
    }

    // 공유 수 증가 시 랭킹 업데이트
    public void incrementRankingByShare(News news) {
        String categoryKey = String.format(NEWS_RANKING_KEY, news.getCategory().name().toLowerCase());
        String globalKey = GLOBAL_RANKING_KEY;
        increaseScore(news.getId(), SHARE_WEIGHT, categoryKey);
        increaseScore(news.getId(), SHARE_WEIGHT, globalKey);
    }

    // 최상위 뉴스 점수 감소 로직
    public void normalizeScores() {
        Set<ZSetOperations.TypedTuple<String>> topNews = redisTemplate.opsForZSet().reverseRangeWithScores(GLOBAL_RANKING_KEY, 0, 0);
        if (topNews != null && !topNews.isEmpty()) {
            Double topScore = topNews.stream().findFirst().get().getScore();
            if (topScore != null) {
                redisTemplate.opsForZSet().rangeWithScores(GLOBAL_RANKING_KEY, 0, -1).forEach(news -> {
                    Double currentScore = news.getScore();
                    Long newsId = Long.valueOf(Objects.requireNonNull(news.getValue()));
                    redisTemplate.opsForZSet().add(GLOBAL_RANKING_KEY, String.valueOf(newsId), currentScore - topScore);
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

