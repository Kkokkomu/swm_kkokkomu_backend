package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.core.config.service.RedisService;
import com.kkokkomu.short_news.core.dto.PageInfoDto;
import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.core.type.EHomeFilter;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.dto.news.request.SharedCntDto;
import com.kkokkomu.short_news.news.dto.news.response.*;
import com.kkokkomu.short_news.news.dto.newsReaction.response.NewReactionByUserDto;
import com.kkokkomu.short_news.news.dto.newsReaction.response.ReactionCntDto;
import com.kkokkomu.short_news.news.repository.NewsRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeNewsService {
    private final NewsRepository newsRepository;

    private final UserLookupService userLookupService;
    private final NewsReactionService newsReactionService;
    private final NewsLookupService newsLookupService;

    private final RedisService redisService;

    /* 홈화면 */
    @Transactional(readOnly = true)
    public PagingResponseDto<List<NewsInfoDto>> readNewsList(Long userId, String category, EHomeFilter filter, int page, int size) {
        User user = userLookupService.findUserById(userId);

        // 일단 최신순으로 조회
        Page<News> results = newsRepository.findAllCreatedAtDesc(PageRequest.of(page, size));

        List<News> news = results.getContent();
        PageInfoDto pageInfo = PageInfoDto.fromPageInfo(results);

        List<NewsInfoDto> newsListDtos = new ArrayList<>();
        for (News newsItem : news) {
            // 각 감정표현 별 갯수
            ReactionCntDto reactionCntDto = newsReactionService.countNewsReaction(newsItem.getId());

            // 유저 감정표현 여부
            NewReactionByUserDto newReactionByUserDto = newsReactionService.checkNewsReaction(userId, newsItem.getId());

            // dto 생성
            newsListDtos.add(
                    NewsInfoDto.builder()
                            .info(NewsWithKeywordDto.of(newsItem))
                            .reactionCnt(reactionCntDto)
                            .userReaction(newReactionByUserDto)
                            .build()
            );
        }

        return PagingResponseDto.fromEntityAndPageInfo(newsListDtos, pageInfo);
    } // 숏폼 리스트 조회

    @Transactional(readOnly = true)
    public PagingResponseDto<List<GuestNewsInfoDto>> guestReadNewsList(int page, int size) {
        // 일단 최신순으로 조회
        Page<News> results = newsRepository.findAllCreatedAtDesc(PageRequest.of(page, size));

        List<News> news = results.getContent();
        PageInfoDto pageInfo = PageInfoDto.fromPageInfo(results);

        List<GuestNewsInfoDto> newsListDtos = new ArrayList<>();
        for (News newsItem : news) {
            // 각 감정표현 별 갯수
            ReactionCntDto reactionCntDto = newsReactionService.countNewsReaction(newsItem.getId());

            // dto 생성
            newsListDtos.add(
                    GuestNewsInfoDto.builder()
                            .info(NewsWithKeywordDto.of(newsItem))
                            .reactionCnt(reactionCntDto)
                            .build()
            );
        }

        return PagingResponseDto.fromEntityAndPageInfo(newsListDtos, pageInfo);
    } // 비로그인 숏폼 리스트 조회



    public NewsDto updateSharedCnt(SharedCntDto sharedCntDto) {
        News news = newsLookupService.findNewsById(sharedCntDto.newsId());

        news.updateSharedCnt();
        newsRepository.save(news);

        return NewsDto.of(news);
    } // 공유 수 증가

    public NewsDto updateNotInterested(SharedCntDto sharedCntDto) {
        News news = newsLookupService.findNewsById(sharedCntDto.newsId());

        return NewsDto.of(news);
    } // 관심없음 표시

    public String increaseNewsView(SharedCntDto sharedCntDto, Long userId) {

        // 레디스 조회수 ++
        redisService.incrementViewCount(sharedCntDto.newsId());
        Integer viewCount = redisService.getViewCount(sharedCntDto.newsId());

        // 시청기록 저장
        redisService.saveNewsViewHistory(userId, sharedCntDto.newsId());

        return "조회수: " + viewCount;
    } // 뉴스 조회

    public void updateViewCnt() {
        log.info("updateViewCnt");
        List<News> newsList = newsRepository.findAll();
        for (News news : newsList) {
            Long newsId = news.getId();
            int redisViewCount = redisService.getViewCount(newsId);
            news.updateViewCnt(redisViewCount); // DB의 조회수 업데이트
            newsRepository.save(news);
        }
    } // 조회수 DB 동기화
}
