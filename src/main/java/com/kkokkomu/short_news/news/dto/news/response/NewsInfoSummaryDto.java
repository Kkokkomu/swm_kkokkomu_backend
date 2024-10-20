package com.kkokkomu.short_news.news.dto.news.response;

import lombok.Builder;

@Builder
public record NewsInfoSummaryDto(
        String sentence_0,
        String sentence_1,
        String sentence_2,
        String prompt_total,
        String Prompt0,
        String Prompt1,
        String Prompt2,
        String sentence_total
) {
}
