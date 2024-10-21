package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.core.config.service.RedisService;
import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.domain.NewsReaction;
import com.kkokkomu.short_news.news.domain.NewsViewHist;
import com.kkokkomu.short_news.news.dto.newsHist.response.NewsHistInfoDto;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.news.dto.newsReaction.request.CreateNewsReactionDto;
import com.kkokkomu.short_news.news.dto.newsReaction.response.NewReactionByUserDto;
import com.kkokkomu.short_news.news.dto.newsReaction.response.NewsReactionDto;
import com.kkokkomu.short_news.news.dto.newsReaction.response.ReactionCntDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.news.repository.NewsReactionRepository;
import com.kkokkomu.short_news.core.type.ENewsReaction;
import com.kkokkomu.short_news.user.service.UserLookupService;
import com.kkokkomu.short_news.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsReactionService {
    private final NewsReactionRepository newsReactionRepository;

    private final UserLookupService userLookupService;
    private final NewsLookupService newsLookupService;
    private final RedisService redisService;

    public NewsReactionDto createNewsReaction(Long userId, CreateNewsReactionDto createNewsReactionDto) {
        log.info("createNewsReaction service");

        User user = userLookupService.findUserById(userId);

        News news = newsLookupService.findNewsById(createNewsReactionDto.newsId());

        if (newsReactionRepository.existsByNewsIdAndUserId(news.getId(), user.getId())) {
            throw new CommonException(ErrorCode.DUPLICATED_NEWS_REACTION);
        }

        NewsReaction newsReaction = newsReactionRepository.save(
                NewsReaction.builder()
                        .user(user)
                        .news(news)
                        .reaction(createNewsReactionDto.reaction())
                        .build()
        );

        // 감정표현 레디스 랭킹 반영
        redisService.incrementRankingByReaction(news);

        return NewsReactionDto.of(newsReaction);
    } // 뉴스 감정표현 생성

    public NewsReactionDto updateNewsReaction(Long userId, CreateNewsReactionDto createNewsReactionDto) {
        log.info("updateNewsReaction service");

        // 유저랑 뉴스 유효성 체크
        User user = userLookupService.findUserById(userId);

        News news = newsLookupService.findNewsById(createNewsReactionDto.newsId());

        // 감정표현 객체 조회 및 업데이트
        NewsReaction newsReaction = newsReactionRepository.findByNewsAndUser(news, user)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS_REACTION));

        newsReaction.updateReaction(createNewsReactionDto.reaction());

        NewsReaction save = newsReactionRepository.save(newsReaction);

        return NewsReactionDto.of(save);
    } // 뉴스 감정표현 수정

    @Transactional
    public String deleteNewsReaction(Long userId, Long newsId, String reaction) {
        User user = userLookupService.findUserById(userId);

        News news = newsLookupService.findNewsById(newsId);

        ENewsReaction newsReaction = ENewsReaction.valueOf(reaction.toUpperCase());

        // 해당 감정표현이 존재하면 삭제
        if (newsReactionRepository.existsByNewsIdAndUserIdAndReaction(news.getId(), user.getId(), newsReaction)) {
            newsReactionRepository.deleteByNewsAndUserAndReaction(news, user, newsReaction);

            // 레디스 랭킹 반영
            redisService.decreaseRankingByReaction(news);

            return "success";
        } else {
            throw new CommonException(ErrorCode.NOT_FOUND_NEWS_REACTION);
        }
    } // 뉴스 감정표현 삭제

    @Transactional(readOnly = true)
    public Page<NewsReaction> getNewsReactionsByCursor(Long userId, Long cursorId, int size) {
        log.info("getNewsWithReaction service");

        PageRequest pageRequest = PageRequest.of(0, size);

        Page<NewsReaction> results;
        if (cursorId == null) {
            // 최초
            results = newsReactionRepository.findAllByUserAndCorsorFirst(userId, pageRequest);
        } else {
            // 그 이후
            if (!newsReactionRepository.existsById(cursorId)) {
                throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
            }
            log.info("cursorId: " + cursorId);

            results = newsReactionRepository.findAllByUserAndCorsor(userId, cursorId, pageRequest);
        }

        return results;
    }

    // 각 감정표현 별 갯수 카운드
    public ReactionCntDto countNewsReaction(Long newsId) {
        return ReactionCntDto.builder()
                .like(newsReactionRepository.countByNewsIdAndReaction(newsId, ENewsReaction.LIKE))
                .angry(newsReactionRepository.countByNewsIdAndReaction(newsId, ENewsReaction.ANGRY))
                .sad(newsReactionRepository.countByNewsIdAndReaction(newsId, ENewsReaction.SAD))
                .surprise(newsReactionRepository.countByNewsIdAndReaction(newsId, ENewsReaction.SURPRISE))
                .build();
    }

    // 유저 감정표현 여부 체크
    public NewReactionByUserDto checkNewsReaction(Long userId, Long newsId) {
        Optional<NewsReaction> newsReaction = newsReactionRepository.findByNewsIdAndUserId(newsId, userId);

        if (newsReaction.isPresent()) {
            ENewsReaction reaction = newsReaction.get().getReaction();
            return NewReactionByUserDto.builder()
                    .id(newsReaction.get().getId())
                    .like(reaction.equals(ENewsReaction.LIKE))
                    .angry(reaction.equals(ENewsReaction.ANGRY))
                    .sad(reaction.equals(ENewsReaction.SAD))
                    .surprise(reaction.equals(ENewsReaction.SURPRISE))
                    .build();
        } else {
            return NewReactionByUserDto.builder()
                    .id(null)
                    .like(false)
                    .angry(false)
                    .sad(false)
                    .surprise(false)
                    .build();
        }
    }
}
