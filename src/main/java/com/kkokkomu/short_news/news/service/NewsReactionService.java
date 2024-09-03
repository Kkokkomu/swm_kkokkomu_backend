package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.domain.NewsReaction;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsReactionService {
    private final NewsReactionRepository newsReactionRepository;

    private final UserLookupService userLookupService;
    private final NewsLookupService newsLookupService;

    public NewsReactionDto createNewsReaction(Long userId, CreateNewsReactionDto createNewsReactionDto) {
        log.info("createNewsReaction service");

        User user = userLookupService.findUserById(userId);

        News news = newsLookupService.findNewsById(createNewsReactionDto.newsId());

        if (newsReactionRepository.existsByNewsIdAndUserIdAndReaction(news.getId(), user.getId(), createNewsReactionDto.reaction())) {
            throw new CommonException(ErrorCode.DUPLICATED_NEWS_REACTION);
        }

        NewsReaction newsReaction = newsReactionRepository.save(
                NewsReaction.builder()
                        .user(user)
                        .news(news)
                        .reaction(createNewsReactionDto.reaction())
                        .build()
        );

        return NewsReactionDto.of(newsReaction);
    } // 뉴스 감정표현 생성

    @Transactional
    public String deleteNewsReaction(Long userId, Long newsId, String reaction) {
        User user = userLookupService.findUserById(userId);

        News news = newsLookupService.findNewsById(newsId);

        ENewsReaction newsReaction = ENewsReaction.valueOf(reaction.toUpperCase());

        // 해당 감정표현이 존재하면 삭제
        if (newsReactionRepository.existsByNewsIdAndUserIdAndReaction(news.getId(), user.getId(), newsReaction)) {
            newsReactionRepository.deleteByNewsAndUserAndReaction(news, user, newsReaction);
            return "success";
        } else {
            throw new CommonException(ErrorCode.NOT_FOUND_NEWS_REACTION);
        }
    } // 뉴스 감정표현 삭제

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
        return NewReactionByUserDto.builder()
                .like(newsReactionRepository.existsByNewsIdAndUserIdAndReaction(newsId, userId, ENewsReaction.LIKE))
                .angry(newsReactionRepository.existsByNewsIdAndUserIdAndReaction(newsId, userId, ENewsReaction.ANGRY))
                .sad(newsReactionRepository.existsByNewsIdAndUserIdAndReaction(newsId, userId, ENewsReaction.SAD))
                .surprise(newsReactionRepository.existsByNewsIdAndUserIdAndReaction(newsId, userId, ENewsReaction.SURPRISE))
                .build();
    }
}
