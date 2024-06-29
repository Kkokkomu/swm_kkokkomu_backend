package com.kkokkomu.short_news.dto.user.request;

import lombok.Builder;
import lombok.NonNull;

public record LoginDto(
        @NonNull
        String uuid
) {
}
