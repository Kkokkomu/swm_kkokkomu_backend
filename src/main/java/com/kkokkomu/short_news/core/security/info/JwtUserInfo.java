package com.kkokkomu.short_news.core.security.info;

import com.kkokkomu.short_news.core.type.EUserRole;
import lombok.Builder;

@Builder
public record JwtUserInfo(Long id, EUserRole role) {
}
