package com.kkokkomu.short_news.user.dto.hideUser.response;

import com.kkokkomu.short_news.user.domain.HideUser;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record SummaryHideUserDto(
        Long id,
        String userName,
        String profileImg,
        String createdAt
) {
    static public SummaryHideUserDto of(HideUser hideUser) {
        return SummaryHideUserDto.builder()
                .id(hideUser.getId())
                .userName(hideUser.getHidedUser().getNickname())
                .profileImg(hideUser.getHidedUser().getProfileImgs().get(0).getImgUrl())
                .createdAt(hideUser.getCreatedAt().toString())
                .build();
    }

    static public List<SummaryHideUserDto> of(List<HideUser> hideUsers) {
        List<SummaryHideUserDto> summaryHideUserDtos = new ArrayList<>();
        for (HideUser hideUser : hideUsers) {
            summaryHideUserDtos.add(SummaryHideUserDto.of(hideUser));
        }
        return summaryHideUserDtos;
    }
}
