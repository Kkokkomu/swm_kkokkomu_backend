package com.kkokkomu.short_news.news.repository;

import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.domain.NewsReaction;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.core.type.ENewsReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsReactionRepository extends JpaRepository<NewsReaction, Long> {
    Long countByNewsIdAndReaction(Long newsId, ENewsReaction reaction);

    Boolean existsByNewsIdAndUserId(Long newsId, Long userId);

    Boolean existsByNewsIdAndUserIdAndReaction(Long newsId, Long userId, ENewsReaction reaction);

    Optional<NewsReaction> findByNewsAndUser(News news, User user);

    void deleteByNewsAndUserAndReaction(News news, User user, ENewsReaction reaction);
}
