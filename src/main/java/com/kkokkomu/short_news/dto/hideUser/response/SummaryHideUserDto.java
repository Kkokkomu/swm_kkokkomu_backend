package com.kkokkomu.short_news.dto.hideUser.response;

import com.kkokkomu.short_news.domain.HideUser;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record SummaryHideUserDto(
        Long id,
        String userName,
        String createdAt
) {
    static public SummaryHideUserDto of(HideUser hideUser) {
        return SummaryHideUserDto.builder()
                .id(hideUser.getId())
                .userName(hideUser.getHidedUser().getNickname())
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
