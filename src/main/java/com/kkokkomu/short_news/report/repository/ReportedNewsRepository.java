package com.kkokkomu.short_news.report.repository;

import com.kkokkomu.short_news.core.type.ECommentProgress;
import com.kkokkomu.short_news.core.type.ENewsProgress;
import com.kkokkomu.short_news.report.domain.ReportedComment;
import com.kkokkomu.short_news.report.domain.ReportedNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedNewsRepository extends JpaRepository<ReportedNews, Long> {
    // 오래된 순 신고된 뉴스 조회 (커서 기반)
    @Query("""
    SELECT rn FROM ReportedNews rn
    WHERE rn.progress = :progress
    AND rn.id > :cursorId
    ORDER BY rn.reportedAt ASC, rn.id ASC
    """)
    Page<ReportedNews> findByProgressOrderByReportedAt(
            @Param("cursorId") Long cursorId,
            @Param("progress") ENewsProgress progress,
            Pageable pageable
    );

    // 오래된 순 신고된 뉴스 최초 페이지 조회 (커서 기반)
    @Query("""
    SELECT rn FROM ReportedNews rn
    WHERE rn.progress = :progress
    ORDER BY rn.reportedAt ASC, rn.id ASC
    """)
    Page<ReportedNews> findFirstPageByProgressOrderByReportedAt(
            @Param("progress") ENewsProgress progress,
            Pageable pageable
    );

    // 최신순 처리완료 댓글 조회 (커서 기반)
    @Query("""
    SELECT rn FROM ReportedNews rn
    WHERE rn.progress = :executed OR rn.progress = :dismissed
    AND rn.id < :cursorId
    ORDER BY rn.reportedAt DESC, rn.id ASC
    """)
    Page<ReportedNews> findByProgressOrderByReportedAtDesc(
            @Param("cursorId") Long cursorId,
            @Param("executed") ENewsProgress executed,
            @Param("dismissed") ENewsProgress dismissed,
            Pageable pageable
    );


    // 최신순 처리완료 댓글 최초 페이지 조회 (커서 기반)
    @Query("""
    SELECT rn FROM ReportedNews rn
    WHERE rn.progress = :executed OR rn.progress = :dismissed
    ORDER BY rn.reportedAt DESC, rn.id ASC
    """)
    Page<ReportedNews> findFirstPageByProgressOrderByReportedAtDesc(
            @Param("executed") ENewsProgress executed,
            @Param("dismissed") ENewsProgress dismissed,
            Pageable pageable
    );
}
