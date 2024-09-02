package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.core.constant.Constant;
import com.kkokkomu.short_news.event.domain.ShareEvent;
import com.kkokkomu.short_news.event.repository.ShareEventRepository;
import com.kkokkomu.short_news.subscription.domain.Subscription;
import com.kkokkomu.short_news.subscription.repository.SubscriptionRepository;
import com.kkokkomu.short_news.user.dto.auth.request.SocialRegisterRequestDto;
import com.kkokkomu.short_news.user.dto.auth.response.AccessTokenDto;
import com.kkokkomu.short_news.user.dto.auth.response.JwtTokenDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.oauth2.OAuth2UserInfo;
import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.core.type.ELoginProvider;
import com.kkokkomu.short_news.core.type.EUserRole;
import com.kkokkomu.short_news.core.util.JwtUtil;
import com.kkokkomu.short_news.core.util.OAuth2Util;
import com.kkokkomu.short_news.core.util.PasswordUtil;
import com.kkokkomu.short_news.user.domain.ProfileImg;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.domain.UserCategory;
import com.kkokkomu.short_news.user.repository.ProfileImgRepository;
import com.kkokkomu.short_news.user.repository.UserCategoryRepository;
import com.kkokkomu.short_news.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kkokkomu.short_news.core.constant.Constant.DEFAULT_PROFILE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final ShareEventRepository shareEventRepository;
    private final ProfileImgRepository profileImgRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserCategoryRepository userCategoryRepository;

    private final OAuth2Util oAuth2Util;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public JwtTokenDto socialRegister(String accessToken, SocialRegisterRequestDto socialRegisterRequestDto) {  // 소셜 로그인 후 회원 등록 및 토큰 발급
        String token = refineToken(accessToken);    // poppin access token

        Long userId = jwtUtil.getUserIdFromToken(token);    // 토큰으로부터 id 추출

        // 소셜 회원가입 시, id와 provider로 유저 정보를 찾음
        User user = userRepository.findByIdAndELoginProvider(userId, socialRegisterRequestDto.provider())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // 닉네임 중복검사
        if (userRepository.findByNickname(socialRegisterRequestDto.nickname()).isPresent()) {
            throw new CommonException(ErrorCode.DUPLICATED_NICKNAME);
        }

        // 닉네임과 생년월일을 등록 -> 소셜 회원가입 완료 / User Role = USER
        user.register(
                socialRegisterRequestDto.nickname(),
                socialRegisterRequestDto.sex(),
                socialRegisterRequestDto.birthday()
        );

        // 공유 이벤트 참여
        ShareEvent shareEvent = shareEventRepository.findByRecommandCode(socialRegisterRequestDto.recommendCode());

        if (shareEvent != null) {
            shareEvent.updateParticipantingCnt();
        }

        // 카테고리 정보 true로 초기화
        List<UserCategory> userCategories = new ArrayList<>();
        for (ECategory eCategory : ECategory.values()) {
            userCategories.add(
                    UserCategory.builder()
                            .user(user)
                            .category(eCategory)
                            .build()
            );
        }
        userCategoryRepository.saveAll(userCategories);


        // 기본 프로필 이미지 등록
        profileImgRepository.save(
                ProfileImg.builder()
                        .user(user)
                        .imgUrl(DEFAULT_PROFILE)
                        .resizeUrl(DEFAULT_PROFILE)
                        .build()
        );

        // 구독 정보 초기화
        subscriptionRepository.save(
                Subscription.builder()
                        .user(user)
                        .isPremium(false)
                        .build()
        );

        // 엑세스, 리프레시 토큰 생성
        final JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.getId(), user.getRole());
        user.updateRefreshToken(jwtTokenDto.refreshToken());

        return jwtTokenDto;
    }

    @Transactional
    public JwtTokenDto refresh(String refreshToken) {
        String token = refineToken(refreshToken);
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (!user.getRefreshToken().equals(token)) {
            throw new CommonException(ErrorCode.INVALID_TOKEN_ERROR);
        }
        JwtTokenDto jwtToken = jwtUtil.generateToken(userId, user.getRole());
        user.updateRefreshToken(jwtToken.refreshToken());
        return jwtToken;
    }

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
