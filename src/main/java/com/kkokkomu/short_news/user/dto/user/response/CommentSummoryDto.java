package com.kkokkomu.short_news.user.dto.user.response;

import com.kkokkomu.short_news.user.domain.User;
import lombok.Builder;

@Builder
public record CommentSummoryDto(
        Long id,
        String profileImg,
        String nickname
) {
    static public CommentSummoryDto of(User user) {
        return CommentSummoryDto.builder()
                .id(user.getId())
                .profileImg(user.getProfileImgs().get(0).getImgUrl())
                .nickname(user.getNickname())
                .build();
    }
}
