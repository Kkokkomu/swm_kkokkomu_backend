package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.ShareEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareEventRepository extends JpaRepository<ShareEvent, Long> {
    ShareEvent findByRecommandCode(String recommandCode);
}
