package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.dto.news.response.SearchNewsDto;
import com.kkokkomu.short_news.news.repository.NewsRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsLogService {
    private final NewsRepository newsRepository;
    private final UserLookupService userLookupService;

    public CursorResponseDto<List<SearchNewsDto>> searchNewsWithComment(Long userId, Long cursorId, int size) {
        log.info("searchNewsWithComment service");

        User user = userLookupService.findUserById(userId);

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

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

    public CursorResponseDto<List<SearchNewsDto>> searchNewsWithReaction(Long userId, Long cursorId, int size) {
        log.info("searchNewsWithReaction service");

        User user = userLookupService.findUserById(userId);

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            // 최초
            results = newsRepository.findFirstPageNewsByUserReactionsOrderByIdDesc(user, pageRequest);
        } else {
            // 그 이후
            results = newsRepository.findNewsByUserReactionsAndIdLessThanOrderByIdDesc(user, cursorId, pageRequest);
        }
        news = results.getContent();

        List<SearchNewsDto> searchNewsDtos = SearchNewsDto.of(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(searchNewsDtos, cursorInfoDto);
    } // 감정표현한 뉴스 조회
}
