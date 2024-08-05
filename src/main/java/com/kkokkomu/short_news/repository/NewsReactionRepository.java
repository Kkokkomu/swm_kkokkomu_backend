package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.NewsReaction;
import com.kkokkomu.short_news.type.ENewsReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsReactionRepository extends JpaRepository<NewsReaction, Long> {
    Long countByNewsIdAndReaction(Long newsId, ENewsReaction reaction);

    Boolean existsByNewsIdAndUserIdAndReaction(Long newsId, Long userId, ENewsReaction reaction);
}
