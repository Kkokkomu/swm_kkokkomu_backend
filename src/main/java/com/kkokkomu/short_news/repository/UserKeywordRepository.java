package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    Optional<UserKeyword> findByUserIdAndKeywordId(Long userId, Long keywordId);

    List<UserKeyword> findAllByUserId(Long userId);
}
