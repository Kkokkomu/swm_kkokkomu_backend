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

    // 글로벌 랭킹 초기화
    public void deleteGlobalRank() {
        redisTemplate.delete(GLOBAL_RANKING_KEY);
    }

    // 전체 랭킹 뉴스 아이디 및 점수 반환
    public Set<ZSetOperations.TypedTuple<String>> getAllGlobalRank() {
        return redisTemplate.opsForZSet().rangeWithScores(GLOBAL_RANKING_KEY, 0, -1);
    }

    // 뉴스 조회수 증가 및 랭킹 업데이트
    public void incrementRankingByView(News news) {
        String categoryKey = String.format(NEWS_RANKING_KEY, news.getCategory().name().toLowerCase());
        String globalKey = GLOBAL_RANKING_KEY;

        incrementViewCount(news.getId());

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

    // 뉴스 생성 시 랭킹 등록
    public void applyRankingByGenerate(News news) {
        String categoryKey = String.format(NEWS_RANKING_KEY, news.getCategory().name().toLowerCase());
        String globalKey = GLOBAL_RANKING_KEY;
        increaseScore(news.getId(), 1L, categoryKey);
        increaseScore(news.getId(), 1L, globalKey);
    }

    // 특정 뉴스 글로벌 랭킹
    public Double getGlobalNewsScore(Long newsId) {
        String key = GLOBAL_RANKING_KEY;
        String newsIdStr = String.valueOf(newsId);
        Double score = redisTemplate.opsForZSet().score(key, newsIdStr);
        return score;
    }

    // 최상위 뉴스 점수 감소 로직
    public void normalizeScores(Double topScore) {
        if (topScore != null) {
            redisTemplate.opsForZSet().rangeWithScores(GLOBAL_RANKING_KEY, 0, -1).forEach(news -> {
                Double currentScore = news.getScore();
                Long newsId = Long.valueOf(Objects.requireNonNull(news.getValue()));
                redisTemplate.opsForZSet().add(GLOBAL_RANKING_KEY, String.valueOf(newsId), currentScore - topScore);
            });
        }
    }

    // 특정 카테고리 랭킹 점수 감소 로직
    public void normalizeCategoryScores(ECategory category) {
        String categoryKey = String.format(NEWS_RANKING_KEY, category.name().toLowerCase());

        Set<ZSetOperations.TypedTuple<String>> topNews = redisTemplate.opsForZSet().reverseRangeWithScores(categoryKey, 0, 0);
        if (topNews != null && !topNews.isEmpty()) {
            Double topScore = topNews.stream().findFirst().get().getScore();
            if (topScore != null) {
                redisTemplate.opsForZSet().rangeWithScores(categoryKey, 0, -1).forEach(news -> {
                    Double currentScore = news.getScore();
                    Long newsId = Long.valueOf(Objects.requireNonNull(news.getValue()));
                    redisTemplate.opsForZSet().add(categoryKey, String.valueOf(newsId), currentScore - topScore);
                });
            }
        }
    }

    // 모든 카테고리 랭킹에 대해 초기화
    public void normalizeALLCategoryScores() {
        Set<ZSetOperations.TypedTuple<String>> topNews = redisTemplate.opsForZSet().reverseRangeWithScores(GLOBAL_RANKING_KEY, 0, 0);
        Double topScore = topNews.stream().findFirst().get().getScore();

        for (ECategory category : ECategory.values()) {
            String categoryKey = String.format(NEWS_RANKING_KEY, category.name().toLowerCase());

            if (topNews != null && !topNews.isEmpty()) {
                if (topScore != null) {
                    redisTemplate.opsForZSet().rangeWithScores(categoryKey, 0, -1).forEach(news -> {
                        Double currentScore = news.getScore();
                        Long newsId = Long.valueOf(Objects.requireNonNull(news.getValue()));
                        redisTemplate.opsForZSet().add(categoryKey, String.valueOf(newsId), currentScore - topScore);
                    });
                }
            }
        }
    }

    // 글로벌 랭킹 보드 반환
    public List<ZSetOperations.TypedTuple<String>> getGlobalNewsRankingWithScores(Double cursorScore, Long cursorId, int size) {
        String key = GLOBAL_RANKING_KEY;

        Set<ZSetOperations.TypedTuple<String>> resultSet;

        // 커서가 없을 경우 처음부터 조회
        if (cursorScore == null && cursorId == null) {
            resultSet = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, size - 1);
            log.info("No cursor - fetching top {} results", size);
        } else {
            log.info("cursorID : {}", cursorId);

            // 커서가 있는 경우, 점수와 뉴스 ID를 기반으로 조회
            resultSet = redisTemplate.opsForZSet()
                    .reverseRangeByScoreWithScores(key, Double.NEGATIVE_INFINITY, cursorScore, 0, size + 1); // 하나 더 가져옴

            // 점수가 같은 경우 커서 ID 이후의 데이터만 필터링
            resultSet = resultSet.stream()
                    .filter(tuple -> {
                        if (tuple.getScore().equals(cursorScore)) {
                            // 점수가 같으면 커서 ID보다 큰 ID만 가져옴
                            return Long.parseLong(tuple.getValue()) < cursorId;
                        }
                        return true;
                    })
                    .collect(Collectors.toSet());

            log.info("resultSet size after filtering: {}", resultSet.size());
        }

        // 중복 방지를 위한 ID 기반 2차 정렬 수행
        return resultSet.stream()
                .sorted((a, b) -> {
                    int scoreComparison = b.getScore().compareTo(a.getScore());
                    if (scoreComparison == 0) {
                        // 점수가 같을 경우 뉴스 ID로 추가 정렬
                        return Long.compare(Long.parseLong(b.getValue()), Long.parseLong(a.getValue()));
                    }
                    return scoreComparison;
                })
                .limit(size)
                .collect(Collectors.toList());
    }

    public List<Long> getNewsIdsForMultipleCategories(List<ECategory> categories, Long cursorId, int size) {
        log.info("getNewsIdsForMultipleCategories");
        log.info("cursorId: " + cursorId);
        Map<Long, Double> newsScores = new HashMap<>(); // 점수 기반 정렬을 위해 HashMap 사용

        for (ECategory category : categories) {
            String rankingKey = String.format(NEWS_RANKING_KEY, category.name().toLowerCase());
            Set<ZSetOperations.TypedTuple<String>> newsIdsWithScores = redisTemplate.opsForZSet()
                    .reverseRangeByScoreWithScores(rankingKey, Double.NEGATIVE_INFINITY,
                            cursorId == null ? Double.POSITIVE_INFINITY : getScore(cursorId) - 1, 0, size + 1);

            if (newsIdsWithScores != null) {
                log.info("{} newsIdsWithScores {}", rankingKey, newsIdsWithScores.size());
            }
            for (ZSetOperations.TypedTuple<String> newsId : newsIdsWithScores) {
                log.info("{} newsId value {}, newsId score {}", rankingKey, newsId.getValue(), newsId.getScore());
            }

            newsIdsWithScores.forEach(idWithScore -> {
                Long newsId = Long.parseLong(idWithScore.getValue());
                Double score = idWithScore.getScore();

                // 중복 제거와 동시에 cursorId 필터링
                if (cursorId == null || !score.equals(getScore(cursorId)) || newsId < cursorId) {
                    newsScores.put(newsId, score);
                }
            });
        }

        log.info("newsScores {}", newsScores.size());

        // 점수 기준으로 내림차순 정렬
        List<Map.Entry<Long, Double>> sortedNewsList = newsScores.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // 점수 기준으로 내림차순
                .collect(Collectors.toList());

        // 정렬된 리스트에서 뉴스 ID만 추출하여 반환
        return sortedNewsList.stream()
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

    public void deleteNewsAllViewHistory(Long userId) {
        String key = VIEW_HISTORY_PREFIX + userId;
        redisTemplate.delete(key);
    }

    public void deleteNewsViewListHistory(Long userId, List<Long> newsIds) {
        String key = VIEW_HISTORY_PREFIX + userId;

        redisTemplate.opsForSet().remove(key, newsIds.stream()
                .map(String::valueOf) // Long -> String 변환
                .toArray());
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

