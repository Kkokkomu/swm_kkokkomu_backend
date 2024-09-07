package com.kkokkomu.short_news.user.controller;

import com.kkokkomu.short_news.core.annotation.UserId;
import com.kkokkomu.short_news.core.dto.ResponseDto;
import com.kkokkomu.short_news.user.dto.user.response.AdminUserDto;
import com.kkokkomu.short_news.user.dto.user.response.MyPageDto;
import com.kkokkomu.short_news.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "유저(관리자)")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class AdminUserController {
    private final UserService userService;

    @Operation(summary = "관리자 유저 리스트 조회")
    @GetMapping("/list")
    public ResponseDto<List<AdminUserDto>> readAdminUserList() {
        log.info("readAdminUserList controller");
        return ResponseDto.ok(userService.findAllUser());
    }
}
