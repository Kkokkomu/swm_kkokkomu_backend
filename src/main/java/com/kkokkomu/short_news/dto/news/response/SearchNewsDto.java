package com.kkokkomu.short_news.dto.news.response;

import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.UserKeyword;
import com.kkokkomu.short_news.dto.keyword.response.SearchKeywordDto;
import com.kkokkomu.short_news.dto.userKeyword.response.UserKeywordDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record SearchNewsDto(
        Long newsId,
        String title,
        String createdAt,
        String thumnailUrl,
        int viewCnt
) {
    static public SearchNewsDto of(News news) {
        return SearchNewsDto.builder()
                .newsId(news.getId())
                .title(news.getTitle())
                .createdAt(news.getCreatedAt().toString())
                .thumnailUrl(news.getThumbnail())
                .viewCnt(news.getViewCnt())
                .build();
    }

    static public List<SearchNewsDto> of(List<News> newsList) {
        List<SearchNewsDto> searchNewsDtos = new ArrayList<>();
        for (News news : newsList) {
            searchNewsDtos.add(SearchNewsDto.of(news));
        }
        return searchNewsDtos;
    }
}
