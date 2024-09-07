package com.kkokkomu.short_news.report.repository;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.core.type.ECommentProgress;
import com.kkokkomu.short_news.report.domain.ReportedComment;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedCommentRepository extends JpaRepository<ReportedComment, Long> {

    // 오래된 순 신고된 댓글 조회 (커서 기반)
    @Query("""
    SELECT rc FROM ReportedComment rc
    WHERE rc.progress = :progress
    AND rc.id > :cursorId
    ORDER BY rc.reportedAt ASC, rc.id ASC
    """)
    Page<ReportedComment> findByProgressOrderByReportedAt(
            @Param("cursorId") Long cursorId,
            @Param("progress") ECommentProgress progress,
            Pageable pageable
    );


    // 오래된 순 신고된 댓글 최초 페이지 조회 (커서 기반)
    @Query("""
    SELECT rc FROM ReportedComment rc
    WHERE rc.progress = :progress
    ORDER BY rc.reportedAt ASC, rc.id ASC
    """)
    Page<ReportedComment> findFirstPageByProgressOrderByReportedAt(
            @Param("progress") ECommentProgress progress,
            Pageable pageable
    );

    // 최신순 처리완료 댓글 조회 (커서 기반)
    @Query("""
    SELECT rc FROM ReportedComment rc
    WHERE rc.progress = :executed OR rc.progress = :unexecuted
    AND rc.id < :cursorId
    ORDER BY rc.reportedAt DESC, rc.id ASC
    """)
    Page<ReportedComment> findByProgressOrderByReportedAtDesc(
            @Param("cursorId") Long cursorId,
            @Param("executed") ECommentProgress executed,
            @Param("unexecuted") ECommentProgress unexecuted,
            Pageable pageable
    );


    // 최신순 처리완료 댓글 최초 페이지 조회 (커서 기반)
    @Query("""
    SELECT rc FROM ReportedComment rc
    WHERE rc.progress = :executed OR rc.progress = :unexecuted
    ORDER BY rc.reportedAt DESC, rc.id ASC
    """)
    Page<ReportedComment> findFirstPageByProgressOrderByReportedAtDesc(
            @Param("executed") ECommentProgress executed,
            @Param("unexecuted") ECommentProgress unexecuted,
            Pageable pageable
    );

    // 작성자랑 댓글로 신고내역 조회
    Boolean existsByCommentAndReporter(Comment comment, User reporter);
}
