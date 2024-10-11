package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserLookupService {
    List<User> findAll();

    User findUserById(Long userId);

    User findAdminUser(Long userId);

    Boolean existsUser(Long userId);
}
