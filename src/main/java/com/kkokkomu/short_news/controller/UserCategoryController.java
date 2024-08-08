package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.annotation.UserId;
import com.kkokkomu.short_news.dto.userCategory.request.UpdateUserCategoryDto;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.userCategory.response.CategoryByUserDto;
import com.kkokkomu.short_news.service.UserCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저 카테고리")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user-category")
public class UserCategoryController {
    private final UserCategoryService userCategoryService;

    @Operation(summary = "유저 카테고리 업데이트")
    @PutMapping("")
    public ResponseDto<String> updateUserCategory(@RequestBody UpdateUserCategoryDto updateUserCategoryDto) {
        log.info("update category : {}", updateUserCategoryDto);
        return ResponseDto.ok(userCategoryService.updateUserCategory(1L, updateUserCategoryDto));
    }

    @Operation(summary = "유저 카테고리 조회")
    @GetMapping("")
    public ResponseDto<CategoryByUserDto> readCategoryByUser(@Parameter(hidden = true) @UserId Long userId) {
        log.info("update readCategoryByUser controller");
        return ResponseDto.ok(userCategoryService.findUserCategoryByUserId(userId));
    }
}
