package com.kkokkomu.short_news.dto.common;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record CursorInfoDto(
        Integer size,
        Boolean isLast
) {
    public static CursorInfoDto fromPageInfo(Page<?> result) {

        return CursorInfoDto.builder()
                .size(result.getPageable().getPageSize())
                .isLast(result.isLast())
                .build();
    }
}