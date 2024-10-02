package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.core.config.service.RedisService;
import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.core.util.CategoryUtil;
import com.kkokkomu.short_news.keyword.service.NewsKeywordService;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.dto.news.response.*;
import com.kkokkomu.short_news.news.dto.newsReaction.response.NewReactionByUserDto;
import com.kkokkomu.short_news.news.dto.newsReaction.response.ReactionCntDto;
import com.kkokkomu.short_news.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kkokkomu.short_news.core.constant.Constant.*;
import static com.kkokkomu.short_news.core.constant.Constant.DATE_WEIGHT;
import static com.kkokkomu.short_news.core.constant.RedisConstant.GLOBAL_RANKING_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchNewsService {
    private final NewsRepository newsRepository;

    private final NewsLookupService newsLookupService;
    private final NewsReactionService newsReactionService;
    private final RedisService redisService;

    private final CategoryUtil categoryUtil;

    /* 탐색화면 */
    @Transactional(readOnly = true)
    public CursorResponseDto<List<NewsInfoDto>> getLatestNewsFilteredByCategory(String category, Long cursorId, int size, Long userId) {

        log.info("getLatestNewsFilteredByCategory service");

        ECategory eCategory = ECategory.valueOf(category.toUpperCase());

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            results = newsRepository.findFirstPageByCategoryOrderByIdDesc(eCategory, pageRequest);
        } else {
            results = newsRepository.findByCategoryAndIdLessThanOrderByIdDesc(eCategory, cursorId, pageRequest);
        }
        news = results.getContent();

        List<NewsInfoDto> newsInfoDtos = getNewsInfo(news, userId);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsInfoDtos, cursorInfoDto);
    } // 탐색 화면 카테고리 필터 최신순

    @Transactional(readOnly = true)
    public CursorResponseDto<List<NewsInfoDto>> getPopularNewsFilteredByCategory(Long cursorId, int size, Long userId) {
        log.info("getPopularNewsFilteredByCategory service");

        // 커서 아이디에 해당하는 뉴스의 점수를 레디스에서 조회
        Double cursorScore = (cursorId != null) ? redisService.getGlobalNewsScore(cursorId) : null;

        // 레디스에서 전체 랭킹 조회, 요청 사이즈보다 하나 더 많은 아이템을 가져옴
        List<ZSetOperations.TypedTuple<String>> rankedNewsWithScores = redisService.getGlobalNewsRankingWithScores(cursorScore, cursorId, size + 1);

        // 로그로 ID와 점수를 출력
        rankedNewsWithScores.forEach(tuple -> {
            Long newsId = Long.parseLong(tuple.getValue());
            Double score = tuple.getScore();
            log.info("News ID: {}, Score: {}", newsId, score);
        });

        List<Long> newsIds = rankedNewsWithScores.stream()
                .map(tuple -> Long.parseLong(tuple.getValue())) // 뉴스 ID 추출
                .collect(Collectors.toList());
        for (Long newsId : newsIds) {
            log.info("News ID: {}", newsId);
        }

        boolean isLast = newsIds.size() <= size;
        if (!isLast) {
            // 마지막 아이템을 제거하여 실제 페이지 사이즈를 유지
            newsIds.remove(newsIds.size() - 1);
        }

        if (newsIds.isEmpty()) {
            return new CursorResponseDto<>(Collections.emptyList(), CursorInfoDto.builder().size(size).isLast(true).build());
        }

        // 데이터베이스에서 뉴스 상세 정보 조회
        List<News> newsList = newsRepository.findAllById(newsIds);

        // 뉴스 ID의 순서에 맞춰 리스트를 정렬
        Map<Long, News> newsMap = newsList.stream()
                .collect(Collectors.toMap(News::getId, news -> news));  // ID를 키로 하는 맵으로 변환

        List<News> sortedNewsList = newsIds.stream()
                .map(newsMap::get)  // newsIds의 순서에 맞춰 맵에서 뉴스 객체를 가져옴
                .toList();


        List<NewsInfoDto> newsInfoDtos = getNewsInfo(sortedNewsList, userId);
        for (NewsInfoDto newsInfoDto : newsInfoDtos) {
            log.info("News Info: {}", newsInfoDto.info().news().id());
        }

        // 커서 정보 계산
        CursorInfoDto cursorInfoDto = CursorInfoDto.builder()
                .size(size)
                .isLast(isLast)
                .build();

        return new CursorResponseDto<>(newsInfoDtos, cursorInfoDto);
    } // 탐색 화면 카테고리 필터 인기순

    @Transactional(readOnly = true)
    public CursorResponseDto<List<GuestNewsInfoDto>> getGuestLatestNewsFilteredByCategory(String category, Long cursorId, int size) {

        log.info("getGuestLatestNewsFilteredByCategory service");

        ECategory eCategory = ECategory.valueOf(category.toUpperCase());

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            results = newsRepository.findFirstPageByCategoryOrderByIdDesc(eCategory, pageRequest);
        } else {
            results = newsRepository.findByCategoryAndIdLessThanOrderByIdDesc(eCategory, cursorId, pageRequest);
        }
        news = results.getContent();

        List<GuestNewsInfoDto> newsInfoDtos = getGuestNewsInfo(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsInfoDtos, cursorInfoDto);
    } // 비로그인 탐색 화면 카테고리 필터 최신순

    @Transactional(readOnly = true)
    public CursorResponseDto<List<GuestNewsInfoDto>> getGuestPopularNewsFilteredByCategory(Long cursorId, int size) {

        log.info("getGuestPopularNewsFilteredByCategory service");

        // 커서 아이디에 해당하는 뉴스의 점수를 레디스에서 조회
        Double cursorScore = (cursorId != null) ? redisService.getGlobalNewsScore(cursorId) : null;

        // 레디스에서 전체 랭킹 조회, 요청 사이즈보다 하나 더 많은 아이템을 가져옴
        List<ZSetOperations.TypedTuple<String>> rankedNewsWithScores = redisService.getGlobalNewsRankingWithScores(cursorScore, cursorId, size + 1);

        List<Long> newsIds = rankedNewsWithScores.stream()
                .map(tuple -> Long.parseLong(tuple.getValue())) // 뉴스 ID 추출
                .collect(Collectors.toList());

        boolean isLast = newsIds.size() <= size;
        if (!isLast) {
            // 마지막 아이템을 제거하여 실제 페이지 사이즈를 유지
            newsIds.remove(newsIds.size() - 1);
        }

        if (newsIds.isEmpty()) {
            return new CursorResponseDto<>(Collections.emptyList(), CursorInfoDto.builder().size(size).isLast(true).build());
        }

        // 데이터베이스에서 뉴스 상세 정보 조회
        // 데이터베이스에서 뉴스 상세 정보 조회
        List<News> newsList = newsRepository.findAllById(newsIds);

        // 뉴스 ID의 순서에 맞춰 리스트를 정렬
        Map<Long, News> newsMap = newsList.stream()
                .collect(Collectors.toMap(News::getId, news -> news));  // ID를 키로 하는 맵으로 변환

        List<News> sortedNewsList = newsIds.stream()
                .map(newsMap::get)  // newsIds의 순서에 맞춰 맵에서 뉴스 객체를 가져옴
                .toList();


        List<GuestNewsInfoDto> newsInfoDtos = getGuestNewsInfo(sortedNewsList);

        // 커서 정보 계산
        CursorInfoDto cursorInfoDto = CursorInfoDto.builder()
                .size(size)
                .isLast(isLast)
                .build();

        return new CursorResponseDto<>(newsInfoDtos, cursorInfoDto);
    } // 비로그인 탐색 화면 카테고리 필터 인기순

    public CursorResponseDto<List<SearchNewsDto>> searchLatestNews(String category, String text, Long cursorId, int size) {
        log.info("searchLatestNews service");

        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<ECategory> categoryList = categoryUtil.getCategoryList(category);

        List<News> news;
        Page<News> results;
        if (cursorId == null) { // 첫 요청
            results = newsRepository.findFirstByKeywordOrderByIdDesc(categoryList, text, pageRequest);
        } else { // 두 번째 이후 요청
            if (newsRepository.existsById(cursorId)){ // 커서 id에 해당하는 뉴스가 있는지 검사
                throw new CommonException(ErrorCode.NOT_FOUND_NEWS);
            }

            results = newsRepository.findByKeywordOrderByIdDesc(categoryList, cursorId, text, pageRequest);
        }
        news = results.getContent();

        List<SearchNewsDto> newsDtos = SearchNewsDto.of(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsDtos, cursorInfoDto);
    } // 최신순 뉴스 검색

    public CursorResponseDto<List<SearchNewsDto>> searchPopularNews(String category, String text, Long cursorId, int size) {
        log.info("searchPopularNews service");

        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<String> categoryList = categoryUtil.getCategoryStringList(category);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            results = newsRepository.findFirstByKeywordOrderByPopularity(categoryList, VIEW_WEIGHT, COMMENT_WEIGHT, REACTION_WEIGHT, SHARE_WEIGHT, DATE_WEIGHT, text, pageRequest);
        } else {
            // cursorScore 계산
            News cursorNews = newsLookupService.findNewsById(cursorId);

            double cursorScore = calculateScore(cursorNews);

            results = newsRepository.findByKeywordOrderByPopularity(categoryList, VIEW_WEIGHT, COMMENT_WEIGHT, REACTION_WEIGHT, SHARE_WEIGHT, DATE_WEIGHT, cursorScore, text, pageRequest);
        }
        news = results.getContent();

        List<SearchNewsDto> newsDtos = SearchNewsDto.of(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsDtos, cursorInfoDto);
    } // 인기순 뉴스 검색

    @Transactional(readOnly = true)
    public NewsInfoDto readNewsInfo(Long userId, Long newsId) {
        News news = newsLookupService.findNewsById(newsId);

        return getNewsInfo(news, userId);
    } // 로그인 뉴스 정보 조회

    @Transactional(readOnly = true)
    public GuestNewsInfoDto guestReadNewsInfo(Long newsId) {
        News news = newsLookupService.findNewsById(newsId);

        return getGuestNewsInfo(news);
    } // 비로그인 뉴스 정보 조회

    // cursorScore 계산 메서드
    private double calculateScore(News news) {
        long daysDifference = ChronoUnit.DAYS.between(news.getCreatedAt(), LocalDateTime.now());

        double score = (news.getViewCnt() * VIEW_WEIGHT) +
                (news.getComments().size() * COMMENT_WEIGHT) +
                (news.getReactions().size() * REACTION_WEIGHT) +
                (news.getSharedCnt() * SHARE_WEIGHT) +
                (daysDifference * DATE_WEIGHT);

        return score;
    }

    public NewsInfoDto getNewsInfo(News news, Long userId) {
        // 각 감정표현 별 갯수
        ReactionCntDto reactionCntDto = newsReactionService.countNewsReaction(news.getId());

        // 유저 감정표현 여부
        NewReactionByUserDto newReactionByUserDto = newsReactionService.checkNewsReaction(userId, news.getId());

        return NewsInfoDto.builder()
                .info(NewsWithKeywordDto.of(news))
                .userReaction(newReactionByUserDto)
                .reactionCnt(reactionCntDto)
                .build();
    }

    public List<NewsInfoDto> getNewsInfo(List<News> newsList, Long userId) {
        List<NewsInfoDto> newsInfoDtos = new ArrayList<>();

        for (News news : newsList) {
            newsInfoDtos.add(
                    getNewsInfo(news, userId)
            );
        }

        return newsInfoDtos;
    }

    public GuestNewsInfoDto getGuestNewsInfo(News news) {
        // 각 감정표현 별 갯수
        ReactionCntDto reactionCntDto = newsReactionService.countNewsReaction(news.getId());

        return GuestNewsInfoDto.builder()
                .info(NewsWithKeywordDto.of(news))
                .reactionCnt(reactionCntDto)
                .build();
    }

    public List<GuestNewsInfoDto> getGuestNewsInfo(List<News> newsList) {
        List<GuestNewsInfoDto> newsInfoDtos = new ArrayList<>();

        for (News news : newsList) {
            newsInfoDtos.add(
                    getGuestNewsInfo(news)
            );
        }

        return newsInfoDtos;
    }
}
