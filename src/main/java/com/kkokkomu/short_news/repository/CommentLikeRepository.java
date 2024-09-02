package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.CommentLike;
import com.kkokkomu.short_news.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    void deleteByCommentAndUser(Comment comment, User user);

    Boolean existsByCommentAndUser(Comment comment, User user);

    Long countByComment(Comment comment);
}
