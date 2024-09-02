package com.kkokkomu.short_news.keyword.repository;

import com.kkokkomu.short_news.keyword.domain.NewsKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsKeywordRepository extends JpaRepository<NewsKeyword, Long> {
    List<NewsKeyword> findAllByNewsId(Long newsId);
}
