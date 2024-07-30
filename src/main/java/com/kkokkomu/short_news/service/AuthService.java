package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.constant.Constant;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.auth.response.AccessTokenDto;
import com.kkokkomu.short_news.dto.auth.response.JwtTokenDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.oauth2.OAuth2UserInfo;
import com.kkokkomu.short_news.repository.UserRepository;
import com.kkokkomu.short_news.type.ELoginProvider;
import com.kkokkomu.short_news.type.EUserRole;
import com.kkokkomu.short_news.util.JwtUtil;
import com.kkokkomu.short_news.util.OAuth2Util;
import com.kkokkomu.short_news.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final OAuth2Util oAuth2Util;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Object authSocialLogin(String token, String provider) {
        String accessToken = refineToken(token);
        String loginProvider = provider.toUpperCase();
        log.info("loginProvider : " + loginProvider);
        OAuth2UserInfo oAuth2UserInfoDto = getOAuth2UserInfo(loginProvider, accessToken);
        return processUserLogin(oAuth2UserInfoDto, ELoginProvider.valueOf(loginProvider));
    }

    private Object processUserLogin(OAuth2UserInfo oAuth2UserInfo, ELoginProvider provider) {
        Optional<User> user = userRepository.findByEmailAndRole(oAuth2UserInfo.email(), EUserRole.USER);
        // 회원 탈퇴 여부 확인
        if (user.isPresent() && user.get().getIsDeleted()) {
            throw new CommonException(ErrorCode.DELETED_USER_ERROR);
        }
        // 다른 소셜에 이미 가입된 계정이 있는지 확인
        if (user.isPresent() && !user.get().getLoginProvider().equals(provider)) {
            throw new CommonException(ErrorCode.DUPLICATED_SOCIAL_ID);
        }
        // USER 권한 + 이메일 정보가 DB에 존재 -> 팝핀 토큰 발급 및 로그인 상태 변경
        if (user.isPresent() && user.get().getLoginProvider().equals(provider)) {
            JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.get().getId(), EUserRole.USER);
            userRepository.updateRefreshTokenAndLoginStatus(user.get().getId(), jwtTokenDto.refreshToken(), true);
            return jwtTokenDto;
        } else {
            // 비밀번호 랜덤 생성 후 암호화해서 DB에 저장
            User newUser = userRepository.findByEmail(oAuth2UserInfo.email())
                    .orElseGet(() -> userRepository.save(
                            User.toGuestEntity(oAuth2UserInfo,
                                    bCryptPasswordEncoder.encode(PasswordUtil.generateRandomPassword()),
                                    provider))
                    );
            // 유저에게 GUEST 권한 주기
            JwtTokenDto jwtTokenDto = jwtUtil.generateToken(newUser.getId(), EUserRole.GUEST);
            String accessToken = jwtTokenDto.accessToken();
            userRepository.updateRefreshTokenAndLoginStatus(newUser.getId(), jwtTokenDto.refreshToken(), true);
            return new AccessTokenDto(accessToken);
        }
    }

    private String refineToken(String accessToken) {
        if (accessToken.startsWith(Constant.BEARER_PREFIX)) {
            return accessToken.substring(Constant.BEARER_PREFIX.length());
        }
        else {
            return accessToken;
        }
    }

    private OAuth2UserInfo getOAuth2UserInfo(String provider, String accessToken){
        if (provider.equals(ELoginProvider.KAKAO.toString())){
            return oAuth2Util.getKakaoUserInfo(accessToken);
//        } else if (provider.equals(ELoginProvider.GOOGLE.toString())) {
//            return oAuth2Util.getGoogleUserInfo(accessToken);
//        } else if (provider.equals(ELoginProvider.APPLE.toString())) {
//            return appleOAuthService.getAppleUserInfo(accessToken);
        }
        else {
            throw new CommonException(ErrorCode.INVALID_OAUTH2_PROVIDER);
        }
    }
}
