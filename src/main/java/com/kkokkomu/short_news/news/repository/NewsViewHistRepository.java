package com.kkokkomu.short_news.news.repository;

import com.kkokkomu.short_news.news.domain.NewsViewHist;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsViewHistRepository extends JpaRepository<NewsViewHist, Long> {
    void deleteAllByUser(User user);

    // 중복 체크를 위한 쿼리
    boolean existsByUserIdAndNewsId(Long userId, Long newsId);
}
