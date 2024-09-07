package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.core.config.service.S3Service;
import com.kkokkomu.short_news.subscription.service.SubscriptionService;
import com.kkokkomu.short_news.user.domain.ProfileImg;
import com.kkokkomu.short_news.subscription.domain.Subscription;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.dto.user.request.BanUserDto;
import com.kkokkomu.short_news.user.dto.user.request.UpdateUserDto;
import com.kkokkomu.short_news.user.dto.user.response.AdminUserDto;
import com.kkokkomu.short_news.user.dto.user.response.MyPageDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.user.dto.user.response.UserDto;
import com.kkokkomu.short_news.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final UserLookupService userLookupService;

    private final SubscriptionService subscriptionService;
    private final ProfileImgService profileImgService;
    private final S3Service s3Service;

    public MyPageDto readMyPageInfo(Long userId) {
        User user = userLookupService.findUserById(userId);

        ProfileImg profileImg = profileImgService.findProfileImgByUser(user);

        Subscription subscription = subscriptionService.findSubscriptionByUser(user);

        String endDate = "";
        if (subscription.getIsPremium()) {
            endDate = subscription.getEndDate().toString();
        }

        return MyPageDto.builder()
                .id(userId)
                .nickname(user.getNickname())
                .email(user.getEmail())
                .isPremium(subscription.getIsPremium())
                .premiumEndDate(endDate)
                .profileImg(profileImg.getImgUrl())
                .build();
    } // 마이페이지 정보 조회

    @Transactional
    public UserDto updateUserProfile(Long userId, UpdateUserDto userDto, MultipartFile profileImg) {
        User user = userLookupService.findUserById(userId);

        profileImgService.putProfileImg(profileImg, user);

        user.updateProfile(
                userDto.nickname(),
                userDto.birthday(),
                userDto.sex()
        );

        return UserDto.of(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserProfile(Long userId) {
        User user = userLookupService.findUserById(userId);
        return UserDto.of(user);
    }

    /* 관리자 */

    public List<AdminUserDto> findAllUser() {
        List<User> users = userRepository.findAllAscId();

        return AdminUserDto.of(users);
    } // 관리자 유저 리스트 조회

    public AdminUserDto banUser(BanUserDto banUserDto, Long userId) {
        User user = userLookupService.findUserById(userId);

        user.banUser(banUserDto.day());

        return AdminUserDto.of(user);
    } // 댓글 작성 금지 부여

    public AdminUserDto clearUser(Long userId) {
        User user = userLookupService.findUserById(userId);

        user.clearUser();

        return AdminUserDto.of(user);
    } // 댓글 작성 금지 부여 해제
}
