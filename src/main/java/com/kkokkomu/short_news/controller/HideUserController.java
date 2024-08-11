package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.annotation.UserId;
import com.kkokkomu.short_news.dto.common.PagingResponseDto;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.hideUser.request.CreateHideUserDto;
import com.kkokkomu.short_news.dto.hideUser.response.HideUserDto;
import com.kkokkomu.short_news.dto.keyword.response.SearchKeywordDto;
import com.kkokkomu.short_news.service.HideUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "유저 차단")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/hide-user")
public class HideUserController {
    private final HideUserService hideUserService;

    @Operation(summary = "유저 차단 등록")
    @PostMapping("")
    public ResponseDto<HideUserDto> addHideUser(@Parameter(hidden = true) @UserId Long userId,
                                                  @RequestBody CreateHideUserDto createHideUserDto) {
        log.info("addHideUser controller");
        return ResponseDto.ok(hideUserService.hideUser(userId, createHideUserDto));
    }

    @Operation(summary = "유저 차단 삭제")
    @DeleteMapping("")
    public ResponseDto<String> deleteHideUser(@RequestParam Long hiddenId ) {
        log.info("deleteHideUser controller");
        return ResponseDto.ok(hideUserService.cancelHideUser(hiddenId));
    }
}
