package com.kkokkomu.short_news.news.service;

import com.kkokkomu.short_news.news.repository.NewsViewHistRepository;
import com.kkokkomu.short_news.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsViewHistService {
    private final NewsViewHistRepository newsViewHistRepository;

    @Transactional
    public void deleteAllByUser(User user) {
        log.info("NewsViewHistService deleteAllByUser start");
        newsViewHistRepository.deleteAllByUser(user);
    }
}
