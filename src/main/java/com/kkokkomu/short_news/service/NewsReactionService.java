package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.NewsReaction;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.newsReaction.request.CreateNewsReactionDto;
import com.kkokkomu.short_news.dto.newsReaction.response.NewsReactionDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.NewsReactionRepository;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsReactionService {
    private final NewsReactionRepository newsReactionRepository;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    public NewsReactionDto createNewsReaction(Long userId, CreateNewsReactionDto createNewsReactionDto) {
        log.info("createNewsReaction service");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        News news = newsRepository.findById(createNewsReactionDto.newsId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));

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
}
