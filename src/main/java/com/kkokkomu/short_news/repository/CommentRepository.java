package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 최신순 조회
    @Query("SELECT c FROM Comment c WHERE c.news.id = :newsId AND c.id < :cursorId ORDER BY c.id DESC")
    List<Comment> findByNewsIdAndIdLessThanOrderByIdDesc(
            @Param("newsId") Long newsId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 최신순 초기화 조회
    @Query("SELECT c FROM Comment c WHERE c.news.id = :newsId ORDER BY c.id DESC")
    List<Comment> findFirstPageByNewsIdOrderByIdDesc(
            @Param("newsId") Long newsId,
            Pageable pageable
    );
}
