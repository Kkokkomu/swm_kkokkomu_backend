package com.kkokkomu.short_news.core.dto;

import io.micrometer.common.lang.Nullable;
import lombok.Builder;

@Builder
public record CursorResponseDto<T>(
        @Nullable T items,
        @Nullable CursorInfoDto pageInfo
) {
    public static <T> CursorResponseDto<T> fromEntityAndPageInfo(T data, CursorInfoDto pageInfoDto){
        return new CursorResponseDto<T>(data, pageInfoDto);
    }
}

