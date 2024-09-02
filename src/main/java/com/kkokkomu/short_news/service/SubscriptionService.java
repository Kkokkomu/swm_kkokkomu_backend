package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Subscription;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.SubscriptionRepository;
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
