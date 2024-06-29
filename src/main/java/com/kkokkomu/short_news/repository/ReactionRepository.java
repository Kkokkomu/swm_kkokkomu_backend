package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.Reaction;
import com.kkokkomu.short_news.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findAllByNewsId(Long newsId);

    Boolean existsByUserAndNewsAndGreatAndHateAndExpectAndSurprise(User user, News news, Boolean great, Boolean hate, Boolean expect, Boolean surprise);

    Optional<Reaction> findByUserAndNewsAndGreatAndHateAndExpectAndSurprise(User user, News news, Boolean great, Boolean hate, Boolean expect, Boolean surprise);

}
