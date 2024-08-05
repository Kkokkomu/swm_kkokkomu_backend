package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Keyword;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.NewsKeyword;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.KeywordRepository;
import com.kkokkomu.short_news.repository.NewsKeywordRepository;
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
    private final KeywordRepository keywordRepository;

    public List<NewsKeyword> registerNewsKeyword(News news, List<String> newsKeywordList) {
        List<NewsKeyword> newsKeywords = new ArrayList<>();
        for (String newsKeyword : newsKeywordList) {

            // 만약 키워드가 2글자이상 20글자이하 알파벳/한글/숫자로 구성된 한 단어가 아니면 등록 안함
            if (!newsKeyword.matches("^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9]{2,20}$")) {
                continue;
            }

            Keyword keyword = keywordRepository.findByKeyword(newsKeyword).orElse(null);
            // 같은 이름의 키워드가 없다면
            if (keyword == null) {
                // 키워드 등록
                keyword = keywordRepository.save(
                        Keyword.builder()
                                .keyword(newsKeyword)
                                .build()
                );
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
}
