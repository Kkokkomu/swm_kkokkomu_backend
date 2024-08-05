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
    private final SubscriptionRepository subscriptionRepository;
    private final ProfileImgRepository profileImgRepository;

    public MyPageDto readMyPageInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        ProfileImg profileImg = profileImgRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PROFILE_IMG));

        Subscription subscription = subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_SUBSCRIPTION));

        String endDate = "";
        if (subscription.getIsPremium()) {
            endDate = subscription.getEndDate().toString();
        }

        return MyPageDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .isPremium(subscription.getIsPremium())
                .premiumEndDate(endDate)
                .profileImg(profileImg.getImgUrl())
                .build();
    }
}
