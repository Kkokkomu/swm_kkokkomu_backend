package com.kkokkomu.short_news.user.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.user.dto.user.request.UpdateUserDto;
import com.kkokkomu.short_news.user.dto.user.response.MyPageDto;
import com.kkokkomu.short_news.user.dto.user.response.UserDto;
import com.kkokkomu.short_news.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "유저")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "마이페이지 유저 정보 조회")
    @GetMapping("/mypage")
    public ResponseDto<MyPageDto> readMyPageInfo(@Parameter(hidden = true) @UserId Long userId) {
        log.info("readMyPageInfo controller userId = {}", userId);
        return ResponseDto.ok(userService.readMyPageInfo(userId));
    }

    @Operation(summary = "유저 프로필 이미지 설정")
    @PutMapping("/img")
    public ResponseDto<UserDto> updateUserProfile(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestPart(value = "image") MultipartFile image
    ) {
        log.info("updateUserProfile controller userId = {}", userId);
        return ResponseDto.ok(userService.updateUserProfileImg(userId, image));
    }

    @Operation(summary = "유저 기본 프로필 이미지로 설정")
    @PutMapping("/img/default")
    public ResponseDto<UserDto> updateUserProfileDefault(
            @Parameter(hidden = true) @UserId Long userId
    ) {
        log.info("updateUserProfile controller userId = {}", userId);
        return ResponseDto.ok(userService.updateUserProfileImgDefault(userId));
    }

    @Operation(summary = "유저 정보 수정")
    @PutMapping("")
    public ResponseDto<UserDto> updateUser(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestBody @Valid UpdateUserDto updateUserDto
    ) {
        log.info("updateUser controller userId = {}", userId);
        return ResponseDto.ok(userService.updateUserProfile(userId, updateUserDto));
    }

    @Operation(summary = "유저 프로필 정보 조회")
    @GetMapping("")
    public ResponseDto<UserDto> readUser(@Parameter(hidden = true) @UserId Long userId) {
        log.info("readUser controller userId = {}", userId);
        return ResponseDto.ok(userService.getUserProfile(userId));
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/exit")
    public ResponseDto<?> deleteUser(@Parameter(hidden = true) @UserId Long userId) {
        userService.softDeleteUser(userId);
        return ResponseDto.ok("회원 탈퇴가 완료되었습니다.");
    }
}
