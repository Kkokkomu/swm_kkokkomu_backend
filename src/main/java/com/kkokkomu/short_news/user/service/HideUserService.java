package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.user.domain.HideUser;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.dto.hideUser.request.CreateHideUserDto;
import com.kkokkomu.short_news.user.dto.hideUser.response.HideUserDto;
import com.kkokkomu.short_news.user.dto.hideUser.response.SummaryHideUserDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.user.repository.HideUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HideUserService {
    private final HideUserRepository hideUserRepository;

    private final UserLookupService userLookupService;

    public HideUserDto hideUser(Long userId, CreateHideUserDto createHideUserDto) {
        User user = userLookupService.findUserById(userId);
        User hidedUser = userLookupService.findUserById(createHideUserDto.hidedUserId());

        HideUser hideUser = hideUserRepository.save(
                HideUser.builder()
                        .user(user)
                        .hidedUser(hidedUser)
                        .build()
        );

        return HideUserDto.of(hideUser);
    } // 유저 차단 생성

    public String cancelHideUser(Long hideUserId) {
        if (!hideUserRepository.existsById(hideUserId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_HIDE_USER);
        }

        hideUserRepository.deleteById(hideUserId);

        return "success";
    } // 유저 차단 해제

    @Transactional(readOnly = true)
    public List<SummaryHideUserDto> readHiddenList(Long userId) {
        User user = userLookupService.findUserById(userId);

        List<HideUser> byUser = hideUserRepository.findByUser(user);

        return SummaryHideUserDto.of(byUser);
    } // 유저 차단 목록 조회
}
