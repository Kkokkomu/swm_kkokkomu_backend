package com.kkokkomu.short_news.security.info;

import com.kkokkomu.short_news.type.EUserRole;
import lombok.Builder;

@Builder
public record JwtUserInfo(Long id, EUserRole role) {
}
