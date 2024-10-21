package com.kkokkomu.short_news.news.repository;

import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.domain.NewsReaction;
import com.kkokkomu.short_news.news.domain.NewsViewHist;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.core.type.ENewsReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsReactionRepository extends JpaRepository<NewsReaction, Long> {
    Long countByNewsIdAndReaction(Long newsId, ENewsReaction reaction);

    Boolean existsByNewsIdAndUserId(Long newsId, Long userId);

    Boolean existsByNewsIdAndUserIdAndReaction(Long newsId, Long userId, ENewsReaction reaction);

    Optional<NewsReaction> findByNewsAndUser(News news, User user);

    @Query("SELECT nr FROM NewsReaction nr " +
            "WHERE nr.user.id = :userId " +
            "ORDER BY nr.id DESC")
    Page<NewsReaction> findAllByUserAndCorsorFirst(Long userId, Pageable pageable);

    @Query("SELECT nr FROM NewsReaction nr " +
            "WHERE nr.user.id = :userId " +
            "AND nr.id < :cursorId " +
            "ORDER BY nr.id DESC")
    Page<NewsReaction> findAllByUserAndCorsor(@Param("userId") Long userId, @Param("cursorId") Long cursorId, Pageable pageable);

    void deleteByNewsAndUserAndReaction(News news, User user, ENewsReaction reaction);
}
