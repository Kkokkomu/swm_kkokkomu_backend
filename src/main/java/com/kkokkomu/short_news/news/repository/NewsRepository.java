package com.kkokkomu.short_news.news.repository;

import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.core.type.ECategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("select n from News n order by n.createdAt desc ")
    Page<News> findAllCreatedAtDesc(Pageable pageable);

    /****************** 탐색화면 카테고리 필터 *************************/

    // 최신순 카테고리 필터 조회
    @Query("SELECT n FROM News n WHERE n.category = :category AND n.id < :cursorId ORDER BY n.id DESC")
    Page<News> findByCategoryAndIdLessThanOrderByIdDesc(
            @Param("category") ECategory category,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 최신순 초기화 카테고리 필터 조회
    @Query("SELECT n FROM News n WHERE n.category = :category ORDER BY n.id DESC")
    Page<News> findFirstPageByCategoryOrderByIdDesc(
            @Param("category") ECategory category,
            Pageable pageable
    );

    // 인기순 필터 쿼리
    @Query(value = """
    SELECT n.* FROM News n
    LEFT JOIN comments c ON n.id = c.news_id
    LEFT JOIN reactions r ON n.id = r.news_id
    WHERE (n.viewCnt * :viewWeight + COUNT(c.id) * :commentWeight + COUNT(r.id) * :reactionWeight + n.sharedCnt * :shareWeight +
    TIMESTAMPDIFF(DAY, n.createdAt, CURRENT_TIMESTAMP) * :dateWeight) < :cursorScore
    OR ((n.viewCnt * :viewWeight + COUNT(c.id) * :commentWeight + COUNT(r.id) * :reactionWeight + n.sharedCnt * :shareWeight +
    TIMESTAMPDIFF(DAY, n.createdAt, CURRENT_TIMESTAMP) * :dateWeight) = :cursorScore AND n.id < :cursorId)
    GROUP BY n.id
    ORDER BY (n.viewCnt * :viewWeight + COUNT(c.id) * :commentWeight + COUNT(r.id) * :reactionWeight + n.sharedCnt * :shareWeight +
    TIMESTAMPDIFF(DAY, n.createdAt, CURRENT_TIMESTAMP) * :dateWeight) DESC, n.id DESC
    """, nativeQuery = true)
    Page<News> findByPopularityLessThan(
            @Param("viewWeight") double viewWeight,
            @Param("commentWeight") double commentWeight,
            @Param("reactionWeight") double reactionWeight,
            @Param("shareWeight") double shareWeight,
            @Param("dateWeight") double dateWeight,
            @Param("cursorScore") double cursorScore,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 첫 페이지 인기순 필터 쿼리
    @Query(value = """
    SELECT n.* FROM News n
    LEFT JOIN comments c ON n.id = c.news_id
    LEFT JOIN reactions r ON n.id = r.news_id
    GROUP BY n.id
    ORDER BY (n.viewCnt * :viewWeight + COUNT(c.id) * :commentWeight + COUNT(r.id) * :reactionWeight + n.sharedCnt * :shareWeight +
    TIMESTAMPDIFF(DAY, n.createdAt, CURRENT_TIMESTAMP) * :dateWeight) DESC, n.id DESC
    """, nativeQuery = true)
    Page<News> findFirstPageByPopularity(
            @Param("viewWeight") double viewWeight,
            @Param("commentWeight") double commentWeight,
            @Param("reactionWeight") double reactionWeight,
            @Param("shareWeight") double shareWeight,
            @Param("dateWeight") double dateWeight,
            Pageable pageable
    );


    /****************** 뉴스 검색 *************************/
    // 최신순 검색
    @Query("""
    SELECT n FROM News n 
    WHERE n.category IN :categories 
    AND n.id < :cursorId 
    AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
         OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))
    ORDER BY n.id DESC
    """)
    Page<News> findByKeywordOrderByIdDesc(
            @Param("categories") List<ECategory> categories,
            @Param("cursorId") Long cursorId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 최신순 검색 초기화
    @Query("""
    SELECT n FROM News n 
    WHERE n.category IN :categories 
    AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
         OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))
    ORDER BY n.id DESC
    """)
    Page<News> findFirstByKeywordOrderByIdDesc(
            @Param("categories") List<ECategory> categories,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(value = """
    SELECT n.* FROM News n
    LEFT JOIN comments c ON n.id = c.news_id
    LEFT JOIN reactions r ON n.id = r.news_id
    WHERE n.category IN :categories
    AND (n.viewCnt * :viewWeight + COUNT(c) * :commentWeight + COUNT(r) * :reactionWeight + n.sharedCnt * :shareWeight +
    TIMESTAMPDIFF(DAY, n.createdAt, CURRENT_TIMESTAMP) * :dateWeight) <= :cursorScore
    AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
         OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))
    GROUP BY n.id
    ORDER BY (n.viewCnt * :viewWeight + COUNT(c) * :commentWeight + COUNT(r) * :reactionWeight + n.sharedCnt * :shareWeight +
    TIMESTAMPDIFF(DAY, n.createdAt, CURRENT_TIMESTAMP) * :dateWeight) DESC, n.id DESC
    """, nativeQuery = true)
    Page<News> findByKeywordOrderByPopularity(
            @Param("categories") List<ECategory> categories,
            @Param("viewWeight") double viewWeight,
            @Param("commentWeight") double commentWeight,
            @Param("reactionWeight") double reactionWeight,
            @Param("shareWeight") double shareWeight,
            @Param("dateWeight") double dateWeight,
            @Param("cursorScore") double cursorScore,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 인기순 검색 초기화
    @Query(value = """
    SELECT n.* FROM News n
    LEFT JOIN comments c ON n.id = c.news_id
    LEFT JOIN reactions r ON n.id = r.news_id
    WHERE n.category IN :categories
    AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
         OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))
    GROUP BY n.id
    ORDER BY (n.viewCnt * :viewWeight + COUNT(c.id) * :commentWeight + COUNT(r.id) * :reactionWeight + n.sharedCnt * :shareWeight +
    TIMESTAMPDIFF(DAY, n.createdAt, CURRENT_TIMESTAMP) * :dateWeight) DESC, n.id DESC
    """, nativeQuery = true)
    Page<News> findFirstByKeywordOrderByPopularity(
            @Param("categories") List<ECategory> categories,
            @Param("viewWeight") double viewWeight,
            @Param("commentWeight") double commentWeight,
            @Param("reactionWeight") double reactionWeight,
            @Param("shareWeight") double shareWeight,
            @Param("dateWeight") double dateWeight,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
