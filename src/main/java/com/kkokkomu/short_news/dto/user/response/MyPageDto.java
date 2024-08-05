package com.kkokkomu.short_news.dto.user.response;

import lombok.Builder;

@Builder
public record MyPageDto(
    String nickname,
    String email,
    Boolean isPremium,
    String premiumEndDate,
    String profileImg
) {
}
