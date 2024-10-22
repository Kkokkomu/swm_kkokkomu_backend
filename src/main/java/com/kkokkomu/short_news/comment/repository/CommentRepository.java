package com.kkokkomu.short_news.comment.repository;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.news.domain.NewsReaction;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 비로그인 최신순 댓글 조회
    @Query("""
    SELECT c FROM Comment c 
    WHERE c.news.id = :newsId 
    AND c.id < :cursorId 
    AND c.parent IS NULL
    ORDER BY c.editedAt DESC, c.id DESC
    """)
    Page<Comment> findByNewsIdAndIdLessThanOrderByEditedAtDescGuest(
            @Param("newsId") Long newsId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );


    // 비로그인 최신순 댓글 초기화 조회
    @Query("""
    SELECT c FROM Comment c 
    WHERE c.news.id = :newsId 
    AND c.parent IS NULL
    ORDER BY c.editedAt DESC, c.id DESC
    """)
    Page<Comment> findFirstPageByNewsIdOrderByEditedAtDescGuest(
            @Param("newsId") Long newsId,
            Pageable pageable
    );

    // 최신순 댓글 조회
    @Query("""
    SELECT c FROM Comment c 
    LEFT JOIN HideUser hu ON hu.hidedUser = c.user AND hu.user = :user
    WHERE c.news.id = :newsId 
    AND c.id < :cursorId 
    AND hu.id IS NULL
    AND c.parent IS NULL
    ORDER BY c.editedAt DESC, c.id DESC
    """)
    Page<Comment> findByNewsIdAndIdLessThanOrderByEditedAtDesc(
            @Param("newsId") Long newsId,
            @Param("cursorId") Long cursorId,
            @Param("user") User user,
            Pageable pageable
    );

    // 최신순 댓글 초기화 조회
    @Query("""
    SELECT c FROM Comment c 
    LEFT JOIN HideUser hu ON hu.hidedUser = c.user AND hu.user = :user
    WHERE c.news.id = :newsId 
    AND hu.id IS NULL
    AND c.parent IS NULL
    ORDER BY c.editedAt DESC, c.id DESC
    """)
    Page<Comment> findFirstPageByNewsIdOrderByEditedAtDesc(
            @Param("newsId") Long newsId,
            @Param("user") User user,
            Pageable pageable
    );



    // 인기순 조회
    @Query("""
    SELECT c FROM Comment c 
    LEFT JOIN c.likes likes 
    LEFT JOIN c.children children 
    LEFT JOIN HideUser hu ON hu.hidedUser = c.user AND hu.user = :user
    WHERE c.news.id = :newsId 
    AND hu.id IS NULL
    AND c.parent IS NULL
    GROUP BY c 
    HAVING (COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) < :cursorScore 
    OR ((COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) = :cursorScore AND c.id < :cursorId) 
    ORDER BY (COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) DESC, c.id DESC
    """)
    Page<Comment> findByNewsIdAndPopularityLessThan(
            @Param("newsId") Long newsId,
            @Param("replyWeight") double replyWeight,
            @Param("likeWeight") double likeWeight,
            @Param("cursorScore") double cursorScore,
            @Param("cursorId") Long cursorId,
            @Param("user") User user,
            Pageable pageable
    );

    // 인기순 초기화 조회
    @Query("""
    SELECT c FROM Comment c 
    LEFT JOIN c.likes likes 
    LEFT JOIN c.children children 
    LEFT JOIN HideUser hu ON hu.hidedUser = c.user AND hu.user = :user
    WHERE c.news.id = :newsId 
    AND c.parent IS NULL
    AND hu.id IS NULL
    GROUP BY c 
    ORDER BY (COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) DESC, c.id DESC
    """)
    Page<Comment> findFirstPageByNewsIdAndPopularity(
            @Param("newsId") Long newsId,
            @Param("replyWeight") Long replyWeight,
            @Param("likeWeight") Long likeWeight,
            @Param("user") User user,
            Pageable pageable
    );

    //  비로그인 인기순 조회
    @Query("""
    SELECT c FROM Comment c 
    LEFT JOIN c.likes likes 
    LEFT JOIN c.children children 
    WHERE c.news.id = :newsId 
    AND c.parent IS NULL
    GROUP BY c 
    HAVING (COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) < :cursorScore 
    OR ((COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) = :cursorScore AND c.id < :cursorId) 
    ORDER BY (COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) DESC, c.id DESC
    """)
    Page<Comment> findByNewsIdAndPopularityLessThanGuest(
            @Param("newsId") Long newsId,
            @Param("replyWeight") Long replyWeight,
            @Param("likeWeight") Long likeWeight,
            @Param("cursorScore") Long cursorScore,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );


    // 비로그인 인기순 초기화 조회
    @Query("""
    SELECT c FROM Comment c 
    LEFT JOIN c.likes likes 
    LEFT JOIN c.children children 
    WHERE c.news.id = :newsId 
    AND c.parent IS NULL
    GROUP BY c 
    ORDER BY (COUNT(children) * :replyWeight + COUNT(likes) * :likeWeight) DESC, c.id DESC
    """)
    Page<Comment> findFirstPageByNewsIdAndPopularityGuest(
            @Param("newsId") Long newsId,
            @Param("replyWeight") double replyWeight,
            @Param("likeWeight") double likeWeight,
            Pageable pageable
    );


    // 오래된 순 대댓글 조회
    @Query("""
    SELECT c 
    FROM Comment c 
    LEFT JOIN HideUser hu ON hu.hidedUser = c.user AND hu.user = :user
    WHERE c.parent = :parent 
    AND c.id > :cursorId 
    AND hu.id IS NULL 
    ORDER BY c.id
    """)
    Page<Comment> findByParentAndIdLessThanOrderById(
            @Param("parent") Comment parent,
            @Param("cursorId") Long cursorId,
            @Param("user") User user,
            Pageable pageable
    );

    // 오래된 순 대댓글 조회 초기화
    @Query("""
    SELECT c 
    FROM Comment c 
    LEFT JOIN HideUser hu ON hu.hidedUser = c.user AND hu.user = :user
    WHERE c.parent = :parent 
    AND hu.id IS NULL 
    ORDER BY c.id
    """)
    Page<Comment> findFirstPageByParentOrderById(
            @Param("parent") Comment parent,
            @Param("user") User user,
            Pageable pageable
    );

    // 비로그인 오래된 순 대댓글 조회
    @Query("""
    SELECT c 
    FROM Comment c 
    WHERE c.parent = :parent 
    AND c.id > :cursorId 
    ORDER BY c.id
    """)
    Page<Comment> findByParentAndIdLessThanOrderByIdGuest(
            @Param("parent") Comment parent,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // 비로그인 오래된 순 대댓글 조회 초기화
    @Query("""
    SELECT c 
    FROM Comment c 
    WHERE c.parent = :parent 
    ORDER BY c.id
    """)
    Page<Comment> findFirstPageByParentOrderByIdGuest(
            @Param("parent") Comment parent,
            Pageable pageable
    );

    // 유저가 좋아요한 댓글만 반환
    @Query("""
    SELECT c
    FROM Comment c
    JOIN CommentLike cl ON c = cl.comment
    WHERE cl.user = :user
    """)
    List<Comment> findByUserAndCommentLike(
            @Param("user") User user
    );

    // 최신순 댓글 단 댓글 조회
    @Query("SELECT c FROM Comment c " +
            "WHERE c.user.id = :userId " +
            "ORDER BY c.id DESC ")
    Page<Comment> findAllByUserAndCorsorFirst(Long userId, Pageable pageable);

    // 최신순 댓글 단 댓글 조회 초기화
    @Query("SELECT c FROM Comment c " +
            "WHERE c.user.id = :userId " +
            "AND c.id < :cursorId " +
            "ORDER BY c.id DESC ")
    Page<Comment> findAllByUserAndCorsor(@Param("userId") Long userId, @Param("cursorId") Long cursorId, Pageable pageable);
}
