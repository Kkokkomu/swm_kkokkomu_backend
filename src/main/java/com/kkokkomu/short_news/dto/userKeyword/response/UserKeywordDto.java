package com.kkokkomu.short_news.dto.userKeyword.response;

import com.kkokkomu.short_news.domain.UserKeyword;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record UserKeywordDto(
        Long id,
        Long userId,
        String keyword
) {
    static public UserKeywordDto of(UserKeyword userKeyword) {
        return UserKeywordDto.builder()
                .id(userKeyword.getId())
                .userId(userKeyword.getUser().getId())
                .keyword(userKeyword.getKeyword().getKeyword())
                .build();
    }

    static public List<UserKeywordDto> ofList(List<UserKeyword> userKeywords) {
        List<UserKeywordDto> userKeywordDtos = new ArrayList<>();
        for (UserKeyword userKeyword : userKeywords) {
            userKeywordDtos.add(UserKeywordDto.of(userKeyword));
        }
        return userKeywordDtos;
    }
}
