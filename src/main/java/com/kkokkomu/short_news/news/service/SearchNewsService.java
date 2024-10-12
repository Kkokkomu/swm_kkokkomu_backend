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
import com.kkokkomu.short_news.user.service.UserLookupService;
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

    private final CategoryUtil categoryUtil;
    private final UserLookupService userLookupService;

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

        PageRequest pageRequest = PageRequest.of(0, size);

        List<News> news;
        Page<News> results;
        if (cursorId == null) { // 첫 요청
            results = newsRepository.findByAllOrderByScoreDescFirst(pageRequest);
        } else { // 두 번째 이후 요청
            Double cursorScore = newsRepository.findById(cursorId)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CURSOR))
                    .getScore();

            results = newsRepository.findByAllOrderByScoreDesc(cursorId, cursorScore, pageRequest);
        }
        news = results.getContent();

        List<NewsInfoDto> newsDtos = getNewsInfo(news, userId);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsDtos, cursorInfoDto);
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
    } // 비로그인 탐색 화면 최신순

    @Transactional(readOnly = true)
    public CursorResponseDto<List<GuestNewsInfoDto>> getGuestPopularNewsFilteredByCategory(Long cursorId, int size) {
        log.info("getGuestPopularNewsFilteredByCategory service");

        PageRequest pageRequest = PageRequest.of(0, size);

        List<News> news;
        Page<News> results;
        if (cursorId == null) { // 첫 요청
            results = newsRepository.findByAllOrderByScoreDescFirst(pageRequest);
        } else { // 두 번째 이후 요청
            Double cursorScore = newsRepository.findById(cursorId)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CURSOR))
                    .getScore();

            results = newsRepository.findByAllOrderByScoreDesc(cursorId, cursorScore, pageRequest);
        }
        news = results.getContent();

        List<GuestNewsInfoDto> newsDtos = getGuestNewsInfo(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsDtos, cursorInfoDto);
    } // 비로그인 탐색 화면 인기순

    @Transactional(readOnly = true)
    public CursorResponseDto<List<NewsInfoDto>> searchLatestNews(String category, String text, Long cursorId, int size, Long userId) {
        log.info("searchLatestNews service");

        if (!userLookupService.existsUser(userId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_USER);
        }

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

        List<NewsInfoDto> newsDtos = getNewsInfo(news, userId);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsDtos, cursorInfoDto);
    } // 최신순 뉴스 검색

    @Transactional(readOnly = true)
    public CursorResponseDto<List<NewsInfoDto>> searchPopularNews(String category, String text, Long cursorId, int size, Long userId) {
        log.info("searchPopularNews service");

        if (!userLookupService.existsUser(userId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_USER);
        }

        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<ECategory> categoryList = categoryUtil.getCategoryList(category);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            results = newsRepository.findFirstByKeywordOrderByPopularity(categoryList, text, pageRequest);
        } else {
            // cursorScore 계산
            News cursorNews = newsLookupService.findNewsById(cursorId);

            double cursorScore = calculateScore(cursorNews);

            results = newsRepository.findByKeywordOrderByPopularity(categoryList, cursorId, cursorScore, text, pageRequest);
        }
        news = results.getContent();

        List<NewsInfoDto> newsDtos = getNewsInfo(news, userId);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsDtos, cursorInfoDto);
    } // 인기순 뉴스 검색

    @Transactional(readOnly = true)
    public CursorResponseDto<List<GuestNewsInfoDto>> guestSearchLatestNews(String category, String text, Long cursorId, int size) {
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

        List<GuestNewsInfoDto> newsDtos = getGuestNewsInfo(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsDtos, cursorInfoDto);
    } // 비로그인 최신순 뉴스 검색

    @Transactional(readOnly = true)
    public CursorResponseDto<List<GuestNewsInfoDto>> guestSearchPopularNews(String category, String text, Long cursorId, int size) {
        log.info("searchPopularNews service");

        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<ECategory> categoryList = categoryUtil.getCategoryList(category);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            results = newsRepository.findFirstByKeywordOrderByPopularity(categoryList, text, pageRequest);
        } else {
            // cursorScore 계산
            News cursorNews = newsLookupService.findNewsById(cursorId);

            double cursorScore = calculateScore(cursorNews);

            results = newsRepository.findByKeywordOrderByPopularity(categoryList, cursorId, cursorScore, text, pageRequest);
        }
        news = results.getContent();

        List<GuestNewsInfoDto> newsDtos = getGuestNewsInfo(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsDtos, cursorInfoDto);
    } // 비로그인 인기순 뉴스 검색

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
