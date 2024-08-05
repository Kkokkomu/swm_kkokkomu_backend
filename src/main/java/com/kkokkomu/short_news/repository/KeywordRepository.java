package com.kkokkomu.short_news.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.kkokkomu.short_news.domain.Keyword;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByKeyword(String keyword);

    @Query(value = "SELECT k.id, k.keyword, " +
            "(SELECT COUNT(uk1.id) FROM user_favorite_keyword uk1 WHERE uk1.keyword_id = k.id) as userCount, " +
            "(SELECT COUNT(uk2.id) FROM user_favorite_keyword uk2 WHERE uk2.keyword_id = k.id AND uk2.created_at >= :lastWeek) as weeklyUserCount " +
            "FROM keyword k " +
            "WHERE k.keyword LIKE %:keyword% " +
            "GROUP BY k.id " +
            "ORDER BY weeklyUserCount DESC",
            countQuery = "SELECT COUNT(k.id) " +
                    "FROM keyword k " +
                    "WHERE k.keyword LIKE %:keyword%",
            nativeQuery = true)
    Page<Object[]> findPopularKeywords(@Param("keyword") String keyword, @Param("lastWeek") LocalDateTime lastWeek, Pageable pageable);
}
