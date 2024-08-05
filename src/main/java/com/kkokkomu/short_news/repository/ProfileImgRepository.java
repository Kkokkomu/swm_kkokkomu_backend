package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.ProfileImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileImgRepository extends JpaRepository<ProfileImg, Long> {
}
