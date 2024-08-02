package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.domain.UserCategory;
import com.kkokkomu.short_news.dto.userCategory.request.UpdateUserCategoryDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.UserCategoryRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import com.kkokkomu.short_news.type.ECategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserCategoryService {
    private final UserCategoryRepository userCategoryRepository;
    private final UserRepository userRepository;

    public String updateUserCategory(Long userId, UpdateUserCategoryDto updateUserCategoryDto) {

        log.info("updateUserCategory start");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // 유저 아이디 기준, 기존 카테고리 다 삭제
        userCategoryRepository.deleteAllByUserId(userId);

        // 요청한 카테고리 추가
        List<UserCategory> userCategories = new ArrayList<>();

        if (updateUserCategoryDto.politics()) {
            userCategories.add(
                    UserCategory.builder()
                            .user(user)
                            .category(ECategory.POLITICS)
                            .build()
            );
            log.info(ECategory.POLITICS.toString());
        }
        if (updateUserCategoryDto.economy()) {
            userCategories.add(
                    UserCategory.builder()
                            .user(user)
                            .category(ECategory.ECONOMY)
                            .build()
            );
            log.info(ECategory.ECONOMY.toString());
        }
        if (updateUserCategoryDto.social()) {
            userCategories.add(
                    UserCategory.builder()
                            .user(user)
                            .category(ECategory.SOCIAL)
                            .build()
            );
            log.info(ECategory.SOCIAL.toString());
        }
        if (updateUserCategoryDto.entertain()) {
            userCategories.add(
                    UserCategory.builder()
                            .user(user)
                            .category(ECategory.ENTERTAIN)
                            .build()
            );
            log.info(ECategory.ENTERTAIN.toString());
        }
        if (updateUserCategoryDto.sports()) {
            userCategories.add(
                    UserCategory.builder()
                            .user(user)
                            .category(ECategory.SPORTS)
                            .build()
            );
            log.info(ECategory.SPORTS.toString());
        }

        userCategoryRepository.saveAll(userCategories);

        return "";
    } // 유저 카테고리 업데이트

}
