package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.ProfileImg;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.ProfileImgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImgService {
    private final ProfileImgRepository profileImgRepository;

    public ProfileImg findProfileImgByUser(User user) {
        return profileImgRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PROFILE_IMG));
    }
}
