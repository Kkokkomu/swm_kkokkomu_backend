package com.kkokkomu.short_news.core.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkokkomu.short_news.alarm.service.FCMTokenService;
import com.kkokkomu.short_news.user.repository.UserRepository;
import com.kkokkomu.short_news.core.security.info.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CustomSignOutProcessHandler implements LogoutHandler {
    private final UserRepository userRepository;

    private final FCMTokenService fcmTokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userRepository.updateRefreshTokenAndLoginStatus(userDetails.getId(), null, false); // 리프레시 토큰 무료화

        removeToken(request, userDetails.getId()); // fcm 토큰 삭제
    }

    public void removeToken(HttpServletRequest request, Long userId) {
        try {
            // 요청의 Body를 Map으로 파싱
            Map<String, String> body = objectMapper.readValue(request.getInputStream(), Map.class);

            String deviceId = body.get("deviceId");

            if (userId != null) {
                System.out.println("로그아웃 처리 중: userId = " + userId);
                // 추가 처리 로직: 유저 세션 무효화 등
            }
            if (deviceId != null) {
                System.out.println("로그아웃 처리 중: token = " + deviceId);
                // 예: 토큰 블랙리스트 처리 등
            }

            fcmTokenService.deleteUserToken(deviceId, userId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("로그아웃 처리 중 오류 발생");
        }
    }
}
