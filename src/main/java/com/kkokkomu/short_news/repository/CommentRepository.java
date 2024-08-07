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

    // 인기순 조회
    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN c.likes likes " +
            "LEFT JOIN c.children children " +
            "WHERE c.news.id = :newsId " +
            "GROUP BY c " +
            "HAVING (COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) < :cursorScore " +
            "OR ((COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) = :cursorScore AND c.id < :cursorId) " +
            "ORDER BY (COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) DESC, c.id DESC")
    List<Comment> findByNewsIdAndPopularityLessThan(
            @Param("newsId") Long newsId,
            @Param("replyWeight") double replyWeight,
            @Param("likeWeight") double likeWeight,
            @Param("cursorScore") double cursorScore,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 인기순 초기화 조회
    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN c.likes likes " +
            "LEFT JOIN c.children children " +
            "WHERE c.news.id = :newsId " +
            "GROUP BY c " +
            "ORDER BY (COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) DESC, c.id DESC")
    List<Comment> findFirstPageByNewsIdAndPopularity(
            @Param("newsId") Long newsId,
            @Param("replyWeight") double replyWeight,
            @Param("likeWeight") double likeWeight,
            Pageable pageable
    );

    // 오래된순 대댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.parent = :parent AND c.id > :cursorId ORDER BY c.id")
    List<Comment> findByParentAndIdLessThanOrderById(
            @Param("parent") Comment parent,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 오래된순 초기화 조회
    @Query("SELECT c FROM Comment c WHERE c.parent = :parent ORDER BY c.id")
    List<Comment> findFirstPageByParentOrderById(
            @Param("parent") Comment parent,
            Pageable pageable
    );


}
