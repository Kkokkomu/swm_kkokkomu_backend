package com.kkokkomu.short_news.repository;

import com.kkokkomu.short_news.domain.Subscription;
import com.kkokkomu.short_news.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUser(User user);
}
