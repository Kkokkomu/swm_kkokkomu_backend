package com.kkokkomu.short_news.core.security.handler;

import com.kkokkomu.short_news.user.repository.UserRepository;
import com.kkokkomu.short_news.core.security.info.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomSignOutProcessHandler implements LogoutHandler {
    private final UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userRepository.updateRefreshTokenAndLoginStatus(userDetails.getId(), null, false);
    }
}
