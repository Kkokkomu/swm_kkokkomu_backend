package com.kkokkomu.short_news.alarm.repository;

import com.kkokkomu.short_news.alarm.domain.AlarmLog;
import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmLogRepository extends JpaRepository<AlarmLog, Long> {
    Long countByReceiverAndIsReadFalse(User receiver);

    List<AlarmLog> findByIdIn(List<Long> ids);

    // 카테고리별 최신순 홈화면 로그 조회
    @Query("SELECT a FROM AlarmLog a " +
            "WHERE a.id < :cursorId " +
            "AND a.receiver = :receiver " +
            "ORDER BY a.id DESC")
    Page<AlarmLog> findPageByReceiver(
            @Param("receiver") User receiver,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 최신순 초기 페이지 로그 조회
    @Query("SELECT a FROM AlarmLog a " +
            "WHERE a.receiver = :receiver " +
            "ORDER BY a.id DESC")
    Page<AlarmLog> findFirstPageByReceiver(
            @Param("receiver") User receiver,
            Pageable pageable
    );
}
