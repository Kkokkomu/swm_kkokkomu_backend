package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.core.dto.PageInfoDto;
import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.core.type.EHomeFilter;
import com.kkokkomu.short_news.core.util.CategoryUtil;
import com.kkokkomu.short_news.keyword.service.NewsKeywordService;
import com.kkokkomu.short_news.news.domain.News;
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
    private final NewsKeywordService newsKeywordService;
    private final NewsReactionService newsReactionService;
    private final NewsLookupService newsLookupService;

    /* 홈화면 */
    public PagingResponseDto<List<NewsListDto>> readNewsList(Long userId, String category, EHomeFilter filter, int page, int size) {
        User user = userLookupService.findUserById(userId);

        // 일단 최신순으로 조회
        Page<News> results = newsRepository.findAllCreatedAtDesc(PageRequest.of(page, size));

        List<News> news = results.getContent();
        PageInfoDto pageInfo = PageInfoDto.fromPageInfo(results);

        List<NewsListDto> newsListDtos = new ArrayList<>();
        for (News newsItem : news) {

            // 뉴스 url 및 기본 정보
            NewsSummaryDto newsSummaryDto = NewsSummaryDto.of(newsItem);

            // 각 감정표현 별 갯수
            ReactionCntDto reactionCntDto = newsReactionService.countNewsReaction(newsItem.getId());

            // 유저 감정표현 여부
            NewReactionByUserDto newReactionByUserDto = newsReactionService.checkNewsReaction(userId, newsItem.getId());

            // dto 생성
            newsListDtos.add(
                    NewsListDto.builder()
                            .shortformList(newsSummaryDto)
                            .reactionCnt(reactionCntDto)
                            .userReaction(newReactionByUserDto)
                            .build()
            );
        }

        return PagingResponseDto.fromEntityAndPageInfo(newsListDtos, pageInfo);
    } // 숏폼 리스트 조회

    public PagingResponseDto<List<GuestNewsListDto>> guestReadNewsList(int page, int size) {
        // 일단 최신순으로 조회
        Page<News> results = newsRepository.findAllCreatedAtDesc(PageRequest.of(page, size));

        List<News> news = results.getContent();
        PageInfoDto pageInfo = PageInfoDto.fromPageInfo(results);

        List<GuestNewsListDto> newsListDtos = new ArrayList<>();
        for (News newsItem : news) {

            // 뉴스 url 및 기본 정보
            NewsSummaryDto newsSummaryDto = NewsSummaryDto.of(newsItem);

            // 각 감정표현 별 갯수
            ReactionCntDto reactionCntDto = newsReactionService.countNewsReaction(newsItem.getId());

            // dto 생성
            newsListDtos.add(
                    GuestNewsListDto.builder()
                            .shortformList(newsSummaryDto)
                            .reactionCnt(reactionCntDto)
                            .build()
            );
        }

        return PagingResponseDto.fromEntityAndPageInfo(newsListDtos, pageInfo);
    } // 비로그인 숏폼 리스트 조회

    @Transactional(readOnly = true)
    public NewsInfoDto readNewsInfo(Long newsId) {
        News news = newsLookupService.findNewsById(newsId);

        // 키워드
        List<String> keywords = newsKeywordService.getStrKeywordListByNewsId(newsId);

        return NewsInfoDto.builder()
                .news(NewsDto.of(news))
                .keywords(keywords)
                .build();
    } // 뉴스 정보 조회
}
