package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
    void deleteAllByUserId(Long userId);
}
