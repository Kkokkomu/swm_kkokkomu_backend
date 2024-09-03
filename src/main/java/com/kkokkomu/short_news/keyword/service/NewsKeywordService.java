package com.kkokkomu.short_news.keyword.service;

import com.kkokkomu.short_news.keyword.domain.Keyword;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.keyword.domain.NewsKeyword;
import com.kkokkomu.short_news.keyword.repository.NewsKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsKeywordService {
    private final NewsKeywordRepository newsKeywordRepository;

    private final KeywordService keywordService;

    public List<NewsKeyword> registerNewsKeyword(News news, List<String> newsKeywordList) {
        List<NewsKeyword> newsKeywords = new ArrayList<>();
        for (String newsKeyword : newsKeywordList) {

            Keyword keyword = keywordService.createKeywordOrNull(newsKeyword);

            if (keyword == null) {
                continue;
            }

            // 뉴스 키워드 등록
            newsKeywords.add(
                    NewsKeyword.builder()
                            .keyword(keyword)
                            .news(news)
                            .build()
            );
        }

        newsKeywordRepository.saveAll(newsKeywords);

        return newsKeywords;
    }

    public List<String> getStrKeywordListByNewsId(Long newsId) {
        return newsKeywordRepository.findAllByNewsId(newsId)
                .stream()
                .map(keyword -> keyword.getKeyword().getKeyword())
                .toList();
    }
}
