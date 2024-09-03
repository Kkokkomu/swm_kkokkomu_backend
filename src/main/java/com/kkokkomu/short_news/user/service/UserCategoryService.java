package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.domain.UserCategory;
import com.kkokkomu.short_news.user.dto.userCategory.request.UpdateUserCategoryDto;
import com.kkokkomu.short_news.user.dto.userCategory.response.CategoryByUserDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.user.repository.UserCategoryRepository;
import com.kkokkomu.short_news.core.type.ECategory;
import jakarta.transaction.Transactional;
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

    private final UserLookupService userLookupService;

    @Transactional
    public String updateUserCategory(Long userId, UpdateUserCategoryDto updateUserCategoryDto) {
        log.info("updateUserCategory start");

        // 모든 카테고리가 false인지 확인
        if (!updateUserCategoryDto.politics() && !updateUserCategoryDto.economy() &&
                !updateUserCategoryDto.social() && !updateUserCategoryDto.entertain() &&
                !updateUserCategoryDto.sports() && !updateUserCategoryDto.living() &&
                !updateUserCategoryDto.world() && !updateUserCategoryDto.it()) {
            throw new CommonException(ErrorCode.INVALID_CATEGORY_SELECTION);
        }

        User user = userLookupService.findUserById(userId);

        // 유저 아이디 기준, 기존 카테고리 다 삭제
        userCategoryRepository.deleteAllByUserId(userId);

        // 요청한 카테고리 추가
        List<UserCategory> userCategories = new ArrayList<>();
        for (ECategory category : ECategory.values()) {
            switch (category) {
                case POLITICS -> {
                    if (updateUserCategoryDto.politics()) {
                        userCategories.add(createUserCategory(user, category));
                    }
                }
                case ECONOMY -> {
                    if (updateUserCategoryDto.economy()) {
                        userCategories.add(createUserCategory(user, category));
                    }
                }
                case SOCIAL -> {
                    if (updateUserCategoryDto.social()) {
                        userCategories.add(createUserCategory(user, category));
                    }
                }
                case ENTERTAIN -> {
                    if (updateUserCategoryDto.entertain()) {
                        userCategories.add(createUserCategory(user, category));
                    }
                }
                case SPORTS -> {
                    if (updateUserCategoryDto.sports()) {
                        userCategories.add(createUserCategory(user, category));
                    }
                }
                case LIVING -> {
                    if (updateUserCategoryDto.living()) {
                        userCategories.add(createUserCategory(user, category));
                    }
                }
                case WORLD -> {
                    if (updateUserCategoryDto.world()) {
                        userCategories.add(createUserCategory(user, category));
                    }
                }
                case IT -> {
                    if (updateUserCategoryDto.it()) {
                        userCategories.add(createUserCategory(user, category));
                    }
                }
            }
            log.info(category.toString());
        }

        userCategoryRepository.saveAll(userCategories);

        return "";
    } // 유저 카테고리 업데이트

    public CategoryByUserDto findUserCategoryByUserId(Long userId) {
        User user = userLookupService.findUserById(userId);

        List<UserCategory> categoryList = userCategoryRepository.findAllByUserId(userId);

        Boolean politics = false;
        Boolean economy = false;
        Boolean social = false;
        Boolean entertain = false;
        Boolean sports = false;
        Boolean living = false;
        Boolean world = false;
        Boolean it = false;
        for (UserCategory userCategory : categoryList) {
            if (userCategory.getCategory().equals(ECategory.POLITICS)) {
                politics = true;
            } else if (userCategory.getCategory().equals(ECategory.ECONOMY)) {
                economy = true;
            } else if (userCategory.getCategory().equals(ECategory.SOCIAL)) {
                social = true;
            } else if (userCategory.getCategory().equals(ECategory.ENTERTAIN)) {
                entertain = true;
            } else if (userCategory.getCategory().equals(ECategory.SPORTS)) {
                sports = true;
            } else if (userCategory.getCategory().equals(ECategory.LIVING)) {
                living = true;
            } else if (userCategory.getCategory().equals(ECategory.WORLD)) {
                world = true;
            } else if (userCategory.getCategory().equals(ECategory.IT)) {
                it = true;
            }
        }

        return CategoryByUserDto.builder()
                .userId(userId)
                .politics(politics)
                .economy(economy)
                .social(social)
                .living(living)
                .world(world)
                .entertain(entertain)
                .it(it)
                .sports(sports)
                .build();
    }

    private UserCategory createUserCategory(User user, ECategory category) {
        return UserCategory.builder()
                .user(user)
                .category(category)
                .build();
    }
}
