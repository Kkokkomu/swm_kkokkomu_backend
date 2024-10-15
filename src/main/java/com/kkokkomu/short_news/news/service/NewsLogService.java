package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.domain.NewsViewHist;
import com.kkokkomu.short_news.news.dto.news.response.NewsInfoDto;
import com.kkokkomu.short_news.news.dto.news.response.SearchNewsDto;
import com.kkokkomu.short_news.news.dto.newsHist.response.NewsHistInfoDto;
import com.kkokkomu.short_news.news.repository.NewsRepository;
import com.kkokkomu.short_news.news.repository.NewsViewHistRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsLogService {
    private final NewsRepository newsRepository;

    private final NewsViewHistService newsViewHistService;
    private final UserLookupService userLookupService;
    private final SearchNewsService searchNewsService;
    private final NewsViewHistRepository newsViewHistRepository;

    public CursorResponseDto<List<SearchNewsDto>> getNewsWithComment(Long userId, Long cursorId, int size) {
        log.info("getNewsWithComment service");

        User user = userLookupService.findUserById(userId);

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        // 캐싱 히스토리 db 동기화
        newsViewHistService.updateNewsHist(userId);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            // 최초
            results = newsRepository.findFirstPageNewsByUserCommentsOrderByIdDesc(user, pageRequest);
        } else {
            // 그 이후
            results = newsRepository.findNewsByUserCommentsAndIdLessThanOrderByIdDesc(user, cursorId, pageRequest);
        }
        news = results.getContent();

        List<SearchNewsDto> searchNewsDtos = SearchNewsDto.of(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(searchNewsDtos, cursorInfoDto);
    } // 댓글 달았던 뉴스 조회

    @Transactional(readOnly = true)
    public CursorResponseDto<List<NewsHistInfoDto>> getNewsWithReaction(Long userId, Long cursorId, int size) {
        log.info("getNewsWithReaction service");

        User user = userLookupService.findUserById(userId);

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        // 캐싱 히스토리 db 동기화
        newsViewHistService.updateNewsHist(userId);

        List<NewsViewHist> news;
        Page<NewsViewHist> results;
        if (cursorId == null) {
            // 최초
            results = newsViewHistRepository.findAllByUserAndCorsorFirst(userId, pageRequest);
        } else {
            // 그 이후
            results = newsViewHistRepository.findAllByUserAndCorsor(userId, cursorId, pageRequest);
        }
        news = results.getContent();

        List<NewsHistInfoDto> searchNewsDtos = getNewsHistInfo(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(searchNewsDtos, cursorInfoDto);
    } // 감정표현한 뉴스 조회

    @Transactional(readOnly = true)
    public CursorResponseDto<List<NewsHistInfoDto>> getNewsWithHist(Long userId, Long cursorId, int size) {
        log.info("getNewsWithHist service");

        User user = userLookupService.findUserById(userId);

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        // 캐싱 히스토리 db 동기화
        newsViewHistService.updateNewsHist(userId);

        List<NewsViewHist> news;
        Page<NewsViewHist> results;
        if (cursorId == null) {
            // 최초
            results = newsViewHistRepository.findAllByUserAndCorsorFirst(userId, pageRequest);
        } else {
            // 그 이후
            results = newsViewHistRepository.findAllByUserAndCorsor(userId, cursorId, pageRequest);
        }
        news = results.getContent();

        List<NewsHistInfoDto> searchNewsDtos = getNewsHistInfo(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(searchNewsDtos, cursorInfoDto);
    } // 감정표현한 뉴스 조회

    @Transactional
    public String deleteNewsHist(String newsHistIdList) {
        log.info("deleteNewsHist service");

        List<Long> split = Arrays.stream(newsHistIdList.split(","))
                .map(Long::parseLong)
                .toList();

        for (Long id : split) {
            log.info("deleteNewsHist id {}", id);
        }

        newsViewHistRepository.deleteAllById(split);

        return "success";
    }

    private NewsHistInfoDto getNewsHistInfo(NewsViewHist newsViewHist) {
        return NewsHistInfoDto.builder()
                .id(newsViewHist.getId())
                .news(searchNewsService.getNewsInfo(newsViewHist.getNews(), newsViewHist.getUser().getId()))
                .build();
    }

    private List<NewsHistInfoDto> getNewsHistInfo(List<NewsViewHist> newsViewHists) {
        List<NewsHistInfoDto> newsHistInfoDtos = new ArrayList<>();
        for (NewsViewHist newsViewHist : newsViewHists) {
            newsHistInfoDtos.add(getNewsHistInfo(newsViewHist));
        }
        return newsHistInfoDtos;
    }
}
