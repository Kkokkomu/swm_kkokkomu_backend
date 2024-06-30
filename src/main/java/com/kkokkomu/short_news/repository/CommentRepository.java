package com.kkokkomu.short_news.repository;
import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByNews(News news, Pageable pageable);
}
