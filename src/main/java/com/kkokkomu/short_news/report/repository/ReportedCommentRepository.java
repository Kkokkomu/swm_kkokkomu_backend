package com.kkokkomu.short_news.report.repository;

import com.kkokkomu.short_news.report.domain.ReportedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedCommentRepository extends JpaRepository<ReportedComment, Long> {
}
