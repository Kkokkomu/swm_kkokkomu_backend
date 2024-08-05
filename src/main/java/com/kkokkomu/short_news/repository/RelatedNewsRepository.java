package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.RelatedNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatedNewsRepository extends JpaRepository<RelatedNews, Long> {
}
