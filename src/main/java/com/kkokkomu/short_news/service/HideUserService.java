package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Comment;
import com.kkokkomu.short_news.domain.HideUser;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.hideUser.request.CreateHideUserDto;
import com.kkokkomu.short_news.dto.hideUser.response.HideUserDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.CommentRepository;
import com.kkokkomu.short_news.repository.HideUserRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HideUserService {
    private final UserRepository userRepository;
    private final HideUserRepository hideUserRepository;

    public HideUserDto hideUser(Long userId, CreateHideUserDto createHideUserDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        User hidedUser = userRepository.findById(createHideUserDto.hidedUserId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TARGET_USER));

        HideUser hideUser = hideUserRepository.save(
                HideUser.builder()
                        .user(user)
                        .hidedUser(hidedUser)
                        .build()
        );

        return HideUserDto.of(hideUser);
    } // 유저 차단 생성

    public String cancelHideUserList(List<Long> hideUserIdList) {
        for (Long hideUserId : hideUserIdList) {
            if (!hideUserRepository.existsById(hideUserId)) {
                throw new CommonException(ErrorCode.NOT_FOUND_HIDE_USER);
            }

            hideUserRepository.deleteById(hideUserId);
        }

        return "success";
    } // 유저 차단 해제
}
