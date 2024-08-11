package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.HideUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HideUserRepository extends JpaRepository<HideUser, Long> {
}
