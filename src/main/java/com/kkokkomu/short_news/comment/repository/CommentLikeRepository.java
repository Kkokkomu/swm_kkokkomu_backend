package com.kkokkomu.short_news.comment.repository;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.comment.domain.CommentLike;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    void deleteByCommentAndUser(Comment comment, User user);

    Boolean existsByCommentAndUser(Comment comment, User user);

    Long countByComment(Comment comment);
}
