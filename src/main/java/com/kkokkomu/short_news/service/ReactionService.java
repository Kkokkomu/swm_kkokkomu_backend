package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.Reaction;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.reaction.request.PostReactionDto;
import com.kkokkomu.short_news.dto.reaction.response.ReactionDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.ReactionRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    public ReactionDto reaction(PostReactionDto postReactionDto) {
        User user = userRepository.findByUuid(postReactionDto.userId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        News news = newsRepository.findById(postReactionDto.newsId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));

        // 이미 등록된 감정표현이 있다면 에러 처리
        if (reactionRepository.existsByUserAndNewsAndGreatAndHateAndExpectAndSurprise(user, news, postReactionDto.great(), postReactionDto.hate(),
                postReactionDto.expect(), postReactionDto.surprise())) {
            throw new CommonException(ErrorCode.DUPLICATED_REACTION);
        }

        Reaction reaction = Reaction.builder()
                .user(user)
                .news(news)
                .great(postReactionDto.great())
                .hate(postReactionDto.hate())
                .expect(postReactionDto.expect())
                .surprise(postReactionDto.surprise())
                .build();

        reactionRepository.save(reaction);

        return ReactionDto.fromEntity(reaction);
    } // 감정표현 생성

    public String deleteReaction(PostReactionDto postReactionDto) {
        User user = userRepository.findByUuid(postReactionDto.userId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        News news = newsRepository.findById(postReactionDto.newsId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));

        Reaction reaction = reactionRepository.findByUserAndNewsAndGreatAndHateAndExpectAndSurprise(user, news, postReactionDto.great(), postReactionDto.hate(),
                postReactionDto.expect(), postReactionDto.surprise())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REACTION));

        reactionRepository.delete(reaction);

        return "success";
    }
}
