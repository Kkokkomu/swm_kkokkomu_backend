package com.kkokkomu.short_news.core.security.service;

import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.user.repository.UserRepository;
import com.kkokkomu.short_news.core.security.info.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return CustomUserDetails.create(user);
    }

    public UserDetails loadUserByUserId(Long userId) {
        User user = userRepository.findByIdAndIsLoginAndRefreshTokenNotNull(userId, true)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return CustomUserDetails.create(user);
    }
}
