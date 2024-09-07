package com.kkokkomu.short_news.keyword.repository;

import com.kkokkomu.short_news.keyword.domain.UserKeyword;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    Optional<UserKeyword> findByUserIdAndKeywordId(Long userId, Long keywordId);

    List<UserKeyword> findAllByUserId(Long userId);

    void deleteAllByUser(User user);
}
