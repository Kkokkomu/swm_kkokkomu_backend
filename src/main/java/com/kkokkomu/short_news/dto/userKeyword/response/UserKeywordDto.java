package com.kkokkomu.short_news.dto.userKeyword.response;

import com.kkokkomu.short_news.domain.UserKeyword;
import lombok.Builder;

@Builder
public record UserKeywordDto(
        Long userId,
        String keyword
) {
    static public UserKeywordDto of(UserKeyword userKeyword) {
        return UserKeywordDto.builder()
                .userId(userKeyword.getUser().getId())
                .keyword(userKeyword.getKeyword().getKeyword())
                .build();
    }
}
