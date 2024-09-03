package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLookupServiceImpl implements UserLookupService {
    private final UserRepository userRepository;

    // 유저가 존재하는지 검사
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
    }
}
