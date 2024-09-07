package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.subscription.service.SubscriptionService;
import com.kkokkomu.short_news.user.domain.ProfileImg;
import com.kkokkomu.short_news.subscription.domain.Subscription;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.dto.user.response.AdminUserDto;
import com.kkokkomu.short_news.user.dto.user.response.MyPageDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final UserLookupService userLookupService;

    private final SubscriptionService subscriptionService;
    private final ProfileImgService profileImgService;

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
    }

    /* 관리자 */

    public List<AdminUserDto> findAllUser() {
        List<User> users = userRepository.findAllAscId();

        return AdminUserDto.of(users);
    } // 관리자 유저 리스트 조회
}
