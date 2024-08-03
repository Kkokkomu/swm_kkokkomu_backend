package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.NewsKeyword;
import com.kkokkomu.short_news.domain.RelatedNews;
import com.kkokkomu.short_news.dto.news.response.GenerateNewsDto;
import com.kkokkomu.short_news.dto.news.response.NewsDto;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.RelatedNewsRepository;
import com.kkokkomu.short_news.type.ECategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {
    private final NewsRepository newsRepository;
    private final RelatedNewsRepository relatedNewsRepository;

    private final NewsKeywordService newsKeywordService;

    // 영상 생성 api
    // 관련 뉴스 링크 저장
    //s3 url 저장
    // 키워드 저장
    public GenerateNewsDto generateNews() {
        News news = News.builder().build();

        newsRepository.save(news);

        // 영상 생성 서버에서 영상 url 및 정보 받아옴

        String summary = "한 대표는 당 정책위의장의 사퇴 문제에 대해 인선은 당 대표의 권한이라고 밝혔습니다.\n 그는 인선 지연의 이유로 특수한 정국을 언급하며, 좋은 정치 수행을 위한 과정이라고 강조했습니다.\n 또한, 인물난 지적에 대해 반박하며 능력 있는 인재들이 많다고 자신감을 드러냈습니다.";
        List<String> keywords = new ArrayList<>(Arrays.asList("인선", "정책위", "변화"));
        String s3Url = "";
        String thumnailUrl = "";
        ECategory category = getCategoryByName("정치");
        String relatedUrl = "https://n.news.naver.com/mnews/article/277/0005454035";

        // 뉴스 키워드 생성
        List<NewsKeyword> newsKeywords = newsKeywordService.registerNewsKeyword(news, keywords);

        // 관련 기사 링크 등록
        RelatedNews relatedNews = relatedNewsRepository.save(
                RelatedNews.builder()
                        .news(news)
                        .relatedUrl(relatedUrl)
                        .build()
        );

        news.update(
                s3Url,
                "",
                "",
                thumnailUrl,
                summary,
                category
        );

        news = newsRepository.save(news);

        return GenerateNewsDto.builder()
                .newsDto(NewsDto.fromEntity(news))
                .keywords(newsKeywords.stream()
                        .map(newsKeyword -> newsKeyword.getKeyword().getKeyword())
                        .toList())
                .relatedUrl(relatedNews.getRelatedUrl())
                .build();
    }
    
    // 카테고리 enum casting
    public ECategory getCategoryByName(String categoryName) {
        ECategory category = null;
        if (Objects.equals(categoryName, "정치")) {
            category = ECategory.POLITICS;
        } else if (Objects.equals(categoryName, "사회")) {
            category = ECategory.SOCIAL;
        } else if (Objects.equals(categoryName, "경제")) {
            category = ECategory.ECONOMY;
        } else if (Objects.equals(categoryName, "생활")) {
            category = ECategory.LIVING;
        } else if (Objects.equals(categoryName, "세계")) {
            category = ECategory.WOLRD;
        } else if (Objects.equals(categoryName, "연예")) {
            category = ECategory.ENTERTAIN;
        } else if (Objects.equals(categoryName, "스포츠")) {
            category = ECategory.SPORTS;
        }

        return category;
    }
}
