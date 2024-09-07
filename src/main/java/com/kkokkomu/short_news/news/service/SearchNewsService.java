package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.core.util.CategoryUtil;
import com.kkokkomu.short_news.keyword.service.NewsKeywordService;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.dto.news.response.SearchNewsDto;
import com.kkokkomu.short_news.news.repository.NewsRepository;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.kkokkomu.short_news.core.constant.Constant.*;
import static com.kkokkomu.short_news.core.constant.Constant.DATE_WEIGHT;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchNewsService {
    private final NewsRepository newsRepository;

    private final NewsLookupService newsLookupService;

    private final CategoryUtil categoryUtil;

    /* 탐색화면 */
    public CursorResponseDto<List<SearchNewsDto>> getLatestNewsFilteredByCategory(String category, Long cursorId, int size) {

        log.info("getfilteredNews service");

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

        List<SearchNewsDto> searchNewsDtos = SearchNewsDto.of(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(searchNewsDtos, cursorInfoDto);
    } // 탐색 화면 카테고리 필터 최신순

    public CursorResponseDto<List<SearchNewsDto>> getPopularNewsFilteredByCategory(Long cursorId, int size) {

        log.info("getPopularNewsFilteredByCategory service");

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            results = newsRepository.findFirstPageByPopularity(VIEW_WEIGHT, COMMENT_WEIGHT, REACTION_WEIGHT, SHARE_WEIGHT, DATE_WEIGHT, pageRequest);
        } else {
            // cursorScore 계산
            News cursorNews = newsLookupService.findNewsById(cursorId);

            double cursorScore = calculateScore(cursorNews);

            results = newsRepository.findByPopularityLessThan(VIEW_WEIGHT, COMMENT_WEIGHT, REACTION_WEIGHT, SHARE_WEIGHT, DATE_WEIGHT, cursorScore, cursorId, pageRequest);
        }
        news = results.getContent();

        List<SearchNewsDto> searchNewsDtos = SearchNewsDto.of(news);
        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(searchNewsDtos, cursorInfoDto);
    } // 탐색 화면 카테고리 필터 인기순

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

    @Transactional(readOnly = true)
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
}
