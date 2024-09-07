package com.kkokkomu.short_news.user.dto.user.response;

import com.kkokkomu.short_news.user.domain.User;
import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String profileUrl,
        String nickname,
        String email,
        String sex,
        String birthday,
        String createdAt,
        String editedAt
) {
    public static UserDto of(User user) {
        return UserDto.builder()
                .id(user.getId())
                .profileUrl(user.getProfileImgs().get(0).getImgUrl())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .sex(user.getSex().toString())
                .birthday(user.getBirthday().toString())
                .createdAt(user.getCreatedAt().toString())
                .editedAt(user.getEditedAt().toString())
                .build();
    }
}
