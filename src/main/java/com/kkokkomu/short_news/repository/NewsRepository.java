package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("select n from News n order by n.createdAt desc ")
    Page<News> findAll(Pageable pageable);
}
