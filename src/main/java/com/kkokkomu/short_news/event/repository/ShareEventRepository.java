package com.kkokkomu.short_news.event.repository;

import com.kkokkomu.short_news.event.domain.ShareEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareEventRepository extends JpaRepository<ShareEvent, Long> {
    ShareEvent findByRecommandCode(String recommandCode);
}
