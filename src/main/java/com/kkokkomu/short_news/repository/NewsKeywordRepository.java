package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.NewsKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsKeywordRepository extends JpaRepository<NewsKeyword, Long> {
}
