package com.kkokkomu.short_news.dto.news.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record GenerateResponseDto(
        Map<String, Object> data,
        String s3
) {
}
