package com.kkokkomu.short_news.dto.hideUser.response;

import com.kkokkomu.short_news.domain.HideUser;
import lombok.Builder;

@Builder
public record HideUserDto(
        Long reporterId,
        Long hidedUserId
) {
    static public HideUserDto of(HideUser hideUser) {
        return HideUserDto.builder()
                .reporterId(hideUser.getUser().getId())
                .hidedUserId(hideUser.getHidedUser().getId())
                .build();
    }
}
