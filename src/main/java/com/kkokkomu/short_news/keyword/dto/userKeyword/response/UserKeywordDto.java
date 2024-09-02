package com.kkokkomu.short_news.keyword.dto.userKeyword.response;

import com.kkokkomu.short_news.keyword.domain.UserKeyword;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record UserKeywordDto(
        Long id,
        String keyword
) {
    static public UserKeywordDto of(UserKeyword userKeyword) {
        return UserKeywordDto.builder()
                .id(userKeyword.getId())
                .keyword(userKeyword.getKeyword().getKeyword())
                .build();
    }

    static public List<UserKeywordDto> of(List<UserKeyword> userKeywords) {
        List<UserKeywordDto> userKeywordDtos = new ArrayList<>();
        for (UserKeyword userKeyword : userKeywords) {
            userKeywordDtos.add(UserKeywordDto.of(userKeyword));
        }
        return userKeywordDtos;
    }
}
