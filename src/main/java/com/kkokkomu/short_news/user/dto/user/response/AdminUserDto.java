package com.kkokkomu.short_news.user.dto.user.response;

import com.kkokkomu.short_news.user.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
public record AdminUserDto(
        Long id,
        String nickname,
        String email,
        String sex,
        String bannedStartAt,
        String bannedEndAt,
        Boolean isBanned,
        int reportedCnt
) {
    static public AdminUserDto of(User user) {
        return AdminUserDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .sex(user.getSex().toString())
                .bannedStartAt(user.getBannedStartAt() != null ? user.getBannedStartAt().toString() : null)
                .bannedEndAt(user.getBannedEndAt() != null ? user.getBannedEndAt().toString() : null)
                .isBanned(user.getBannedEndAt() != null && user.getBannedEndAt().isAfter(LocalDateTime.now()))
                .reportedCnt(user.getReportedCnt())
                .build();
    }

    static public List<AdminUserDto> of(List<User> users) {
        List<AdminUserDto> adminUserDtos = new ArrayList<>();
        for (User user : users) {
            adminUserDtos.add(AdminUserDto.of(user));
        }
        return adminUserDtos;
    }
}
