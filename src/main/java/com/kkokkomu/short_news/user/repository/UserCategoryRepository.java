package com.kkokkomu.short_news.user.repository;

import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.domain.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
    void deleteAllByUserId(Long userId);

    void deleteAllByUser(User user);

    List<UserCategory> findAllByUserId(Long userId);
}
