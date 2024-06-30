package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.CommentLike;
import com.kkokkomu.short_news.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Boolean existsByCommentAndUser(Comment comment, User user);

    CommentLike findByCommentAndUser(Comment comment, User user);
}
