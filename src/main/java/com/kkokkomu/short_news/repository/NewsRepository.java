package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.type.ECategory;
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
//
//    @Query("SELECT n FROM News n " +
//            "LEFT JOIN n.comments comments " +
//            "LEFT JOIN n.reactions reactions " +
//            "WHERE (n.viewCnt * :viewWeight + COUNT(comments) * :commentWeight + COUNT(reactions) * :reactionWeight + n.sharedCnt * :shareWeight + TIMESTAMPDIFF(MINUTE, n.createdAt, CURRENT_TIMESTAMP) * :timeWeight) < :cursorScore " +
//            "OR ((n.viewCnt * :viewWeight + COUNT(comments) * :commentWeight + COUNT(reactions) * :reactionWeight + n.sharedCnt * :shareWeight + TIMESTAMPDIFF(MINUTE, n.createdAt, CURRENT_TIMESTAMP) * :timeWeight) = :cursorScore AND n.id < :cursorId) " +
//            "GROUP BY n " +
//            "ORDER BY (n.viewCnt * :viewWeight + COUNT(comments) * :commentWeight + COUNT(reactions) * :reactionWeight + n.sharedCnt * :shareWeight + TIMESTAMPDIFF(MINUTE, n.createdAt, CURRENT_TIMESTAMP) * :timeWeight) DESC, n.id DESC")
//    List<News> findByPopularityLessThan(
//            @Param("newsId") Long newsId,
//            @Param("viewWeight") double viewWeight,
//            @Param("commentWeight") double commentWeight,
//            @Param("reactionWeight") double reactionWeight,
//            @Param("shareWeight") double shareWeight,
//            @Param("timeWeight") double timeWeight,
//            @Param("cursorScore") double cursorScore,
//            @Param("cursorId") Long cursorId,
//            Pageable pageable
//    );
//
//    @Query("SELECT n FROM News n " +
//            "LEFT JOIN n.comments comments " +
//            "LEFT JOIN n.reactions reactions " +
//            "GROUP BY n " +
//            "ORDER BY (n.viewCnt * :viewWeight + COUNT(comments) * :commentWeight + COUNT(reactions) * :reactionWeight + n.sharedCnt * :shareWeight + TIMESTAMPDIFF(MINUTE, n.createdAt, CURRENT_TIMESTAMP) * :timeWeight) DESC, n.id DESC")
//    List<News> findFirstPageByPopularity(
//            @Param("newsId") Long newsId,
//            @Param("viewWeight") double viewWeight,
//            @Param("commentWeight") double commentWeight,
//            @Param("reactionWeight") double reactionWeight,
//            @Param("shareWeight") double shareWeight,
//            @Param("timeWeight") double timeWeight,
//            Pageable pageable
//    );

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
    Page<News> findByCKeywordOrderByIdDesc(
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
    Page<News> findFirstPageByKeywordOrderByIdDesc(
            @Param("categories") List<ECategory> categories,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
