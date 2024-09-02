package com.kkokkomu.short_news.subscription.service;

import com.kkokkomu.short_news.subscription.domain.Subscription;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public Subscription findSubscriptionByUser(User user) {
        return subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_SUBSCRIPTION));
    }
}
