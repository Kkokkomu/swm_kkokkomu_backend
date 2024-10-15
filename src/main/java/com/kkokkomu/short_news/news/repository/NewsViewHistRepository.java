package com.kkokkomu.short_news.news.repository;

import com.kkokkomu.short_news.news.domain.NewsViewHist;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsViewHistRepository extends JpaRepository<NewsViewHist, Long> {

    @Modifying
    @Query("DELETE FROM NewsViewHist nh WHERE nh.user.id = :userId AND nh.news.id IN :newsIds")
    void deleteByUserAndNewsIds(@Param("userId") Long userId, @Param("newsIds") List<Long> newsIds);

    void deleteAllByUser(User user);

    // 뉴스 시청기록 최신순 조회
    @Query("SELECT nh FROM NewsViewHist nh " +
            "WHERE nh.user.id = :userId " +
            "ORDER BY nh.id ")
    Page<NewsViewHist> findAllByUserAndCorsorFirst(Long userId, Pageable pageable);

    // 뉴스 시청기록 최신순 조회 초기화
    @Query("SELECT nh FROM NewsViewHist nh " +
            "WHERE nh.user.id = :userId " +
            "AND nh.id > :cursorId " +
            "ORDER BY nh.id ")
    Page<NewsViewHist> findAllByUserAndCorsor(@Param("userId") Long userId, @Param("cursorId") Long cursorId, Pageable pageable);

    // 중복 체크를 위한 쿼리
    boolean existsByUserIdAndNewsId(Long userId, Long newsId);
}
