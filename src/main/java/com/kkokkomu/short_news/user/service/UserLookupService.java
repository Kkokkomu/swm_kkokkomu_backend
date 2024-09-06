package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public interface UserLookupService {
    User findUserById(Long userId);

    User findAdminUser(Long userId);
}
