package com.kkokkomu.short_news.news.repository;

import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.user.domain.User;
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

    /****************** 뉴스 시청 기록 *************************/

    // 시청했던 뉴스 조회
    @Query("""
    SELECT DISTINCT n FROM News n
    JOIN n.newsViewHists v
    WHERE v.user = :user
    AND n.id < :cursorId
    ORDER BY n.id DESC
    """)
    Page<News> findNewsByUserViewHistoryAndIdLessThanOrderByIdDesc(
            @Param("user") User user,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 시청했던 뉴스 최초 조회
    @Query("""
    SELECT DISTINCT n FROM News n
    JOIN n.newsViewHists v
    WHERE v.user = :user
    ORDER BY n.id DESC
    """)
    Page<News> findFirstPageNewsByUserViewHistoryOrderByIdDesc(
            @Param("user") User user,
            Pageable pageable
    );


    // 댓글을 달았던 뉴스 조회
    @Query("""
    SELECT DISTINCT n FROM News n
    JOIN n.comments c
    WHERE c.user = :user
    AND n.id < :cursorId
    ORDER BY n.id DESC
    """)
    Page<News> findNewsByUserCommentsAndIdLessThanOrderByIdDesc(
            @Param("user") User user,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 댓글을 달았던 뉴스 최초 조회
    @Query("""
    SELECT DISTINCT n FROM News n
    JOIN n.comments c
    WHERE c.user = :user
    ORDER BY n.id DESC
    """)
    Page<News> findFirstPageNewsByUserCommentsOrderByIdDesc(
            @Param("user") User user,
            Pageable pageable
    );

    // 감정표현했던 뉴스 조회
    @Query("""
    SELECT DISTINCT n FROM News n
    JOIN n.reactions r
    WHERE r.user = :user
    AND n.id < :cursorId
    ORDER BY n.id DESC
    """)
    Page<News> findNewsByUserReactionsAndIdLessThanOrderByIdDesc(
            @Param("user") User user,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 감정표현했던 뉴스 최초 조회
    @Query("""
    SELECT DISTINCT n FROM News n
    JOIN n.reactions r
    WHERE r.user = :user
    ORDER BY n.id DESC
    """)
    Page<News> findFirstPageNewsByUserReactionsOrderByIdDesc(
            @Param("user") User user,
            Pageable pageable
    );


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
SELECT n.id, n.shortform_url, n.youtube_url, n.instagram_url, n.thumbnail, 
       n.view_cnt, n.title, n.summary, n.shared_cnt, n.category, 
       n.created_at, n.edited_at, n.related_url,
       (n.view_cnt * :viewWeight + COUNT(c.id) * :commentWeight + COUNT(r.id) * :reactionWeight + 
       n.shared_cnt * :shareWeight + TIMESTAMPDIFF(DAY, n.created_at, CURRENT_TIMESTAMP) * :dateWeight) AS popularityScore
FROM news n
LEFT JOIN comment c ON n.id = c.news_id
LEFT JOIN news_reaction r ON n.id = r.news_id
GROUP BY n.id
HAVING popularityScore < :cursorScore
   OR (popularityScore = :cursorScore AND n.id < :cursorId)
ORDER BY popularityScore DESC, n.id DESC
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
SELECT n.id, n.shortform_url, n.youtube_url, n.instagram_url, n.thumbnail, 
       n.view_cnt, n.title, n.summary, n.shared_cnt, n.category, 
       n.created_at, n.edited_at, n.related_url,
       (n.view_cnt * :viewWeight + COUNT(c.id) * :commentWeight + COUNT(r.id) * :reactionWeight + 
       n.shared_cnt * :shareWeight + TIMESTAMPDIFF(DAY, n.created_at, CURRENT_TIMESTAMP) * :dateWeight) AS popularityScore
FROM news n
LEFT JOIN comment c ON n.id = c.news_id
LEFT JOIN news_reaction r ON n.id = r.news_id
GROUP BY n.id
ORDER BY popularityScore DESC, n.id DESC
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
    SELECT n.*, 
           (n.view_cnt * :viewWeight + COUNT(c.id) * :commentWeight + COUNT(r.id) * :reactionWeight + n.shared_cnt * :shareWeight +
            TIMESTAMPDIFF(DAY, n.created_at, CURRENT_TIMESTAMP) * :dateWeight) AS popularity_score
    FROM news n
    LEFT JOIN comment c ON n.id = c.news_id
    LEFT JOIN news_reaction r ON n.id = r.news_id
    WHERE n.category IN :categories
    AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
         OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))
    GROUP BY n.id
    HAVING popularity_score <= :cursorScore
    ORDER BY popularity_score DESC, n.id DESC
    """, nativeQuery = true)
    Page<News> findByKeywordOrderByPopularity(
            @Param("categories") List<String> categories,
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
    SELECT n.* FROM news n
    LEFT JOIN comment c ON n.id = c.news_id
    LEFT JOIN news_reaction r ON n.id = r.news_id
    WHERE n.category IN :categories
    AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))
    GROUP BY n.id
    ORDER BY (n.view_cnt * :viewWeight + COUNT(c.id) * :commentWeight + COUNT(r.id) * :reactionWeight + n.shared_cnt * :shareWeight +
    TIMESTAMPDIFF(DAY, n.created_at, CURRENT_TIMESTAMP) * :dateWeight) DESC, n.id DESC
    """, nativeQuery = true)
    Page<News> findFirstByKeywordOrderByPopularity(
            @Param("categories") List<String> categories,
            @Param("viewWeight") double viewWeight,
            @Param("commentWeight") double commentWeight,
            @Param("reactionWeight") double reactionWeight,
            @Param("shareWeight") double shareWeight,
            @Param("dateWeight") double dateWeight,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
