package com.kkokkomu.short_news.user.repository;

import com.kkokkomu.short_news.user.domain.HideUser;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HideUserRepository extends JpaRepository<HideUser, Long> {
    List<HideUser> findByUser(User user);

    Boolean existsByUserAndHidedUser(User user, User hideUser);
}
