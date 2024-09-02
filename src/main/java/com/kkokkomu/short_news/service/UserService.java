package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.ProfileImg;
import com.kkokkomu.short_news.domain.Subscription;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.user.response.MyPageDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.ProfileImgRepository;
import com.kkokkomu.short_news.repository.SubscriptionRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final SubscriptionService subscriptionService;
    private final ProfileImgService profileImgService;

    public MyPageDto readMyPageInfo(Long userId) {
        User user = findUserById(userId);

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

    // 유저가 존재하는지 검사
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
    }
}
