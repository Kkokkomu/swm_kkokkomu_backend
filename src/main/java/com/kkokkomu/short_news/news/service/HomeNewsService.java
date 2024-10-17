package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.core.config.service.RedisService;
import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.PageInfoDto;
import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.core.type.EHomeFilter;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.domain.NewsViewHist;
import com.kkokkomu.short_news.news.dto.news.request.SharedCntDto;
import com.kkokkomu.short_news.news.dto.news.response.*;
import com.kkokkomu.short_news.news.dto.newsReaction.response.NewReactionByUserDto;
import com.kkokkomu.short_news.news.dto.newsReaction.response.ReactionCntDto;
import com.kkokkomu.short_news.news.repository.NewsRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserCategoryService;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeNewsService {
    private final NewsRepository newsRepository;

    private final UserLookupService userLookupService;
    private final NewsReactionService newsReactionService;
    private final NewsLookupService newsLookupService;
    private final NewsViewHistService newsViewHistService;
    private final SearchNewsService searchNewsService;
    private final UserCategoryService userCategoryService;

    private final RedisService redisService;

    /* 홈화면 */
    @Transactional
    public PagingResponseDto<List<NewsInfoDto>> readNewsList(Long userId, Long cursorId, int size) {
        User user = userLookupService.findUserById(userId);

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        // 유저가 설정했던 카테고리 조회
        List<ECategory> categories = userCategoryService.findAllCategoriesByUserId(userId);

        // 최신순으로 조회
        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            log.info("cursorId is null");
            // 뉴스 조회 기록 캐싱 동기화
            newsViewHistService.updateNewsHist(userId);

            results = newsRepository.findFirstPageByCategoryAndNotViewedByUser(categories, pageRequest);
        } else {
            log.info("cursorId is " + cursorId);
            results = newsRepository.findByCategoryAndIdLessThanAndNotViewedByUser(categories, cursorId, pageRequest);
        }

        log.info("results: " + results);

        // 뉴스 결과물 기반으로 반환
        news = results.getContent();
        PageInfoDto pageInfo = PageInfoDto.fromPageInfo(results);

        log.info("pageInfo: " + pageInfo);

        List<NewsInfoDto> newsListDtos = searchNewsService.getNewsInfo(news, userId);

        log.info("newsListDtos: " + newsListDtos);

        return PagingResponseDto.fromEntityAndPageInfo(newsListDtos, pageInfo);
    } // 숏폼 리스트 최신순 조회

    @Transactional
    public PagingResponseDto<List<NewsInfoDto>> readNewsPopularList(Long userId, Long cursorId, int size) {
        log.info("readNewsPopularList service");

        // 유저가 설정한 카테고리 조회
        List<ECategory> categories = userCategoryService.findAllCategoriesByUserId(userId);

        if (cursorId == null) {
            // 뉴스 조회 기록 캐싱 동기화
            newsViewHistService.updateNewsHist(userId);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        // 인기순으로 조회
        List<News> news;
        Page<News> results;
        if (cursorId == null) { // 첫 요청
            results = newsRepository.findByAllOrderByScoreAndCategoryDescFirst(categories, pageRequest);
        } else { // 두 번째 이후 요청
            Double cursorScore = newsRepository.findById(cursorId)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CURSOR))
                    .getScore();

            results = newsRepository.findByAllOrderByScoreAndCategoryDesc(categories, cursorId, cursorScore, pageRequest);
        }
        news = results.getContent();

        List<NewsInfoDto> newsDtos = searchNewsService.getNewsInfo(news, userId);

        PageInfoDto pageInfo = PageInfoDto.fromPageInfo(results);

        return PagingResponseDto.fromEntityAndPageInfo(newsDtos, pageInfo);
    } // 숏폼 리스트 인기순 조회

    @Transactional
    public PagingResponseDto<List<GuestNewsInfoDto>> guestReadNewsList(Long cursorId, int size) {
        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        // 최신순으로 조회
        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            results = newsRepository.guestFindFirstPageByCategoryAndNotViewedByUser(pageRequest);
        } else {
            results = newsRepository.guestFindByCategoryAndIdLessThanAndNotViewedByUser(cursorId, pageRequest);
        }

        news = results.getContent();
        PageInfoDto pageInfo = PageInfoDto.fromPageInfo(results);

        List<GuestNewsInfoDto> newsListDtos = searchNewsService.getGuestNewsInfo(news);

        return PagingResponseDto.fromEntityAndPageInfo(newsListDtos, pageInfo);
    } // 비로그인 숏폼 리스트 조회

    public NewsDto updateSharedCnt(SharedCntDto sharedCntDto) {
        News news = newsLookupService.findNewsById(sharedCntDto.newsId());

        news.updateSharedCnt();
        newsRepository.save(news);

        // 레디스 반영
        redisService.incrementRankingByShare(news);

        return NewsDto.of(news);
    } // 공유 수 증가

    public NewsDto updateNotInterested(SharedCntDto sharedCntDto) {
        News news = newsLookupService.findNewsById(sharedCntDto.newsId());

        return NewsDto.of(news);
    } // 관심없음 표시

    public String increaseNewsView(SharedCntDto sharedCntDto, Long userId) {
        News news = newsLookupService.findNewsById(sharedCntDto.newsId());

        // 레디스 조회수 ++ㅜㅜ
        redisService.incrementRankingByView(news);
        Integer viewCount = redisService.getViewCount(sharedCntDto.newsId());

        // 시청기록 저장
        redisService.saveNewsViewHistory(userId, sharedCntDto.newsId());

        return "조회수: " + viewCount;
    } // 뉴스 조회

    public String guestIncreaseNewsView(SharedCntDto sharedCntDto) {
        News news = newsLookupService.findNewsById(sharedCntDto.newsId());

        // 레디스 조회수 ++
        redisService.incrementRankingByView(news);
        Integer viewCount = redisService.getViewCount(sharedCntDto.newsId());

        return "조회수: " + viewCount;
    } // 비로그인 뉴스 조회

    public void updateViewCnt() {
        log.info("updateViewCnt");
        List<News> newsList = newsRepository.findAll();
        for (News news : newsList) {
            Long newsId = news.getId();
            int redisViewCount = redisService.getViewCount(newsId);
            news.updateViewCnt(redisViewCount); // DB의 조회수 업데이트
            newsRepository.save(news);
            redisService.resetViewCount(newsId);
        }
    } // 조회수 DB 동기화
}
