package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.comment.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.comment.service.CommentService;
import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.domain.NewsReaction;
import com.kkokkomu.short_news.news.domain.NewsViewHist;
import com.kkokkomu.short_news.news.dto.news.response.NewsInfoDto;
import com.kkokkomu.short_news.news.dto.news.response.SearchNewsDto;
import com.kkokkomu.short_news.news.dto.newsHist.response.CommentHistInfoDto;
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
    private final NewsViewHistRepository newsViewHistRepository;

    private final NewsViewHistService newsViewHistService;
    private final UserLookupService userLookupService;
    private final SearchNewsService searchNewsService;
    private final NewsReactionService newsReactionService;
    private final NewsLookupService newsLookupService;
    private final CommentService commentService;

    @Transactional(readOnly = true)
    public CursorResponseDto<List<CommentHistInfoDto>> getNewsWithComment(Long userId, Long cursorId, int size) {
        log.info("getNewsWithComment service");

        User user = userLookupService.findUserById(userId);

        // 캐싱 히스토리 db 동기화
        newsViewHistService.updateNewsHist(userId);

        // 유저가 감정표현한 뉴스들 조회 by cursot
        Page<Comment> reactionsByCursor = commentService.getNewsCommentByCursor(userId, cursorId, size);
        List<Comment> comments = reactionsByCursor.getContent();

        List<CommentHistInfoDto> searchNewsDtos = getCommentHistInfo(userId, comments);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(reactionsByCursor);

        return CursorResponseDto.fromEntityAndPageInfo(searchNewsDtos, cursorInfoDto);
    } // 댓글 단 뉴스 조회

    @Transactional(readOnly = true)
    public CursorResponseDto<List<NewsInfoDto>> getNewsWithReaction(Long userId, Long cursorId, int size) {
        log.info("getNewsWithReaction service");

        User user = userLookupService.findUserById(userId);

        // 캐싱 히스토리 db 동기화
        newsViewHistService.updateNewsHist(userId);

        // 유저가 감정표현한 뉴스들 조회 by cursot
        Page<NewsReaction> reactionsByCursor = newsReactionService.getNewsReactionsByCursor(userId, cursorId, size);

        List<News> newsList = reactionsByCursor.stream()
                .map(r -> r.getNews())
                .toList();

        // 뉴스들 기반 시청기록 조회
        List<NewsInfoDto> newsHistList = searchNewsService.getNewsInfo(newsList, userId);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(reactionsByCursor);

        return CursorResponseDto.fromEntityAndPageInfo(newsHistList, cursorInfoDto);
    } // 감정표현한 뉴스 조회

    @Transactional(readOnly = true)
    public CursorResponseDto<List<NewsHistInfoDto>> getNewsWithHist(Long userId, Long cursorId, int size) {
        log.info("getNewsWithHist service");
        log.info("getNewsWithReaction service");

        User user = userLookupService.findUserById(userId);

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsLookupService.existNewsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        // 캐싱 히스토리 db 동기화
        newsViewHistService.updateNewsHist(userId);

        List<NewsViewHist> hist;
        Page<NewsViewHist> results;
        if (cursorId == null) {
            // 최초
            results = newsViewHistRepository.findAllByUserAndCorsorFirst(userId, pageRequest);
        } else {
            // 그 이후
            results = newsViewHistRepository.findAllByUserAndCorsor(userId, cursorId, pageRequest);
        }
        hist = results.getContent();

        List<NewsHistInfoDto> searchNewsDtos = getNewsHistInfo(hist);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(searchNewsDtos, cursorInfoDto);
    } // 최신순 시청기록 조회

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

    @Transactional
    public String deleteNewsHistByUserId(Long userId) {
        log.info("deleteNewsHistByUserId service");
        User user = userLookupService.findUserById(userId);

        newsViewHistRepository.deleteAllByUser(user);

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

    private CommentHistInfoDto getCommentHistInfo(Long userId, Comment comment) {
        return CommentHistInfoDto.builder()
                .news(searchNewsService.getNewsInfo(comment.getNews(), userId))
                .comment(CommentDto.of(comment))
                .build();
    }

    private List<CommentHistInfoDto> getCommentHistInfo(Long userId, List<Comment> comments) {
        List<CommentHistInfoDto> newsHistInfoDtos = new ArrayList<>();
        for (Comment comment : comments) {
            newsHistInfoDtos.add(getCommentHistInfo(userId, comment));
        }
        return newsHistInfoDtos;
    }
}
