package com.kkokkomu.short_news.core.config.service;

import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.news.domain.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.kkokkomu.short_news.core.constant.Constant.*;
import static com.kkokkomu.short_news.core.constant.RedisConstant.*;

@Service
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /****** 뉴스 랭킹 ******/

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

    // 공유 수 증가 시 랭킹 업데이트
    public void applyRankingByShare(News news) {
        String categoryKey = String.format(NEWS_RANKING_KEY, news.getCategory().name().toLowerCase());
        String globalKey = GLOBAL_RANKING_KEY;
        increaseScore(news.getId(), 0L, categoryKey);
        increaseScore(news.getId(), 0L, globalKey);
    }

    // 특정 뉴스 글로벌 랭킹
    public Double getGlobalNewsScore(Long newsId) {
        String key = GLOBAL_RANKING_KEY;
        String newsIdStr = String.valueOf(newsId);
        Double score = redisTemplate.opsForZSet().score(key, newsIdStr);
        return score;
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

    // 글로벌 랭킹 보드 반환
    public Set<String> getGlobalNewsRanking(Double cursorScore, int size) {
        return (cursorScore == null) ?
                redisTemplate.opsForZSet().reverseRange(GLOBAL_RANKING_KEY, 0, size - 1) :
                redisTemplate.opsForZSet().reverseRangeByScore(GLOBAL_RANKING_KEY, 0, cursorScore, 0, size);
    }

    public List<Long> getNewsIdsForMultipleCategories(List<ECategory> categories, Long cursorId, int size) {
        log.info("getNewsIdsForMultipleCategories");
        Map<Long, Double> newsScores = new HashMap<>();
        for (ECategory category : categories) {
            String rankingKey = String.format(NEWS_RANKING_KEY, category.name().toLowerCase());
            Set<ZSetOperations.TypedTuple<String>> newsIdsWithScores = redisTemplate.opsForZSet()
                    .reverseRangeByScoreWithScores(rankingKey, cursorId == null ? Double.POSITIVE_INFINITY : getScore(cursorId) - 1, Double.NEGATIVE_INFINITY, 0, size+1);

            newsIdsWithScores.forEach(idWithScore ->
                    newsScores.put(Long.parseLong(idWithScore.getValue()), idWithScore.getScore()));
        }
        log.info("newsScores {}",newsScores.size());

        // 정렬하고 상위 N개만 반환
        return newsScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(size)
                .collect(Collectors.toList());
    }

    private double getScore(Long newsId) {
        return Optional.ofNullable(redisTemplate.opsForZSet().score(GLOBAL_RANKING_KEY, String.valueOf(newsId)))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CURSOR));
    }


    /****** 뉴스 조회수 ******/

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

    /****** 유저 인증코드 ******/
    public void saveCodeWithUserId(String code, Long userId) {
        String key = USER_VALIDATION_CODE_PREFIX + code; // code를 키로 사용
        redisTemplate.opsForValue().set(key, userId.toString(), 10, TimeUnit.MINUTES);
    } // 인증코드를 키로 해서 유저 아이디 저장

    public Long getUserIdByCode(String code) {
        String key = USER_VALIDATION_CODE_PREFIX + code;
        String userIdString = redisTemplate.opsForValue().get(key);
        if (userIdString != null) {
            redisTemplate.delete(key); // 코드 사용 후 삭제
            return Long.parseLong(userIdString);
        }
        return null;
    }

}

