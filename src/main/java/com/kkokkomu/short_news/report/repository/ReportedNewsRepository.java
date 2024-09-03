package com.kkokkomu.short_news.report.repository;

import com.kkokkomu.short_news.report.domain.ReportedNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedNewsRepository extends JpaRepository<ReportedNews, Long> {
}
