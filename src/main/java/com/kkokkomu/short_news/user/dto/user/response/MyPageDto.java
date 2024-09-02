package com.kkokkomu.short_news.user.dto.user.response;

import lombok.Builder;

@Builder
public record MyPageDto(
    Long id,
    String nickname,
    String email,
    Boolean isPremium,
    String premiumEndDate,
    String profileImg
) {
}
