package com.kkokkomu.short_news.user.repository;

import com.kkokkomu.short_news.user.domain.ProfileImg;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileImgRepository extends JpaRepository<ProfileImg, Long> {
    Optional<ProfileImg> findByUser(User user);

    void deleteAllByUser(User user);
}
