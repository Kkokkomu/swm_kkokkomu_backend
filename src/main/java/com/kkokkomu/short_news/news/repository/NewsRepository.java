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
    /****************** 뉴스 홈화면 *************************/
//    @Query("select n from News n order by n.createdAt desc ")
//    Page<News> findAllCreatedAtDesc(Pageable pageable);

    // 카테고리별 최신순 홈화면 뉴스 조회
    @Query("SELECT n FROM News n " +
            "WHERE n.category IN :categories " +
            "AND n.id < :cursorId " +
            "ORDER BY n.id DESC")
    Page<News> findByCategoryAndIdLessThanAndNotViewedByUser(
            @Param("categories") List<ECategory> category,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 카테고리별 최신순 홈화면 뉴스 초기 페이지 조회
    @Query("SELECT n FROM News n " +
            "WHERE n.category IN :categories " +
            "ORDER BY n.id DESC")
    Page<News> findFirstPageByCategoryAndNotViewedByUser(
            @Param("categories") List<ECategory> category,
            Pageable pageable
    );

    // 비로그인 카테고리별 최신순 홈화면 뉴스 조회
    @Query("SELECT n FROM News n " +
            "WHERE n.id < :cursorId " +
            "ORDER BY n.id DESC")
    Page<News> guestFindByCategoryAndIdLessThanAndNotViewedByUser(
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 비로그인 카테고리별 최신순 홈화면 뉴스 초기 페이지 조회
    @Query("SELECT n FROM News n " +
            "ORDER BY n.id DESC")
    Page<News> guestFindFirstPageByCategoryAndNotViewedByUser(
            Pageable pageable
    );

    // 카테고리별 인기순 홈화면 뉴스 조회
    @Query("""
    SELECT n FROM News n 
    WHERE ((n.score < :score) OR (n.score = :score AND n.id > :cursorId))
    AND n.category IN :categories
    ORDER BY n.score DESC, n.id
    """)
    Page<News> findByAllOrderByScoreAndCategoryDesc(
            @Param("categories") List<ECategory> category,
            @Param("cursorId") Long cursorId,
            @Param("score") Double score,
            Pageable pageable
    );

    // 카테고리별 인기순 홈화면 뉴스 조회 초기화
    @Query("""
    SELECT n FROM News n 
    WHERE n.category IN :categories
    ORDER BY n.score DESC, n.id
    """)
    Page<News> findByAllOrderByScoreAndCategoryDescFirst(
            @Param("categories") List<ECategory> category,
            Pageable pageable
    );

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

    // 인기순 탐색 쿼리
    @Query("""
    SELECT n FROM News n 
    WHERE (n.score < :score) OR (n.score = :score AND n.id > :cursorId)
    ORDER BY n.score DESC, n.id
    """)
    Page<News> findByAllOrderByScoreDesc(
            @Param("cursorId") Long cursorId,
            @Param("score") Double score,
            Pageable pageable
    );

    // 인기순 탐색 쿼리 초기화
    @Query("""
    SELECT n FROM News n 
    ORDER BY n.score DESC, n.id
    """)
    Page<News> findByAllOrderByScoreDescFirst(
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
    SELECT n FROM News n 
    WHERE ((n.score < :score) OR (n.score = :score AND n.id > :cursorId))
      AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
         OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND n.category IN :categories
    ORDER BY n.score DESC, n.id
    """, nativeQuery = true)
    Page<News> findByKeywordOrderByPopularity(
            @Param("categories") List<String> categories,
            @Param("score") Double score,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 인기순 검색 초기화
    @Query(value = """
    SELECT n FROM News n 
    WHERE (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
         OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND n.category IN :categories
    ORDER BY n.score DESC, n.id
    """, nativeQuery = true)
    Page<News> findFirstByKeywordOrderByPopularity(
            @Param("categories") List<String> categories,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
