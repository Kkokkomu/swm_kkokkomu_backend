package com.kkokkomu.short_news.service;

import com.google.api.services.youtube.model.PageInfo;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.NewsKeyword;
import com.kkokkomu.short_news.domain.RelatedNews;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.common.PageInfoDto;
import com.kkokkomu.short_news.dto.common.PagingResponseDto;
import com.kkokkomu.short_news.dto.news.response.GenerateNewsDto;
import com.kkokkomu.short_news.dto.news.response.NewsDto;
import com.kkokkomu.short_news.dto.news.response.NewsListDto;
import com.kkokkomu.short_news.dto.news.response.NewsSummaryDto;
import com.kkokkomu.short_news.dto.newsReaction.response.NewReactionByUserDto;
import com.kkokkomu.short_news.dto.newsReaction.response.ReactionCntDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.NewsReactionRepository;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.RelatedNewsRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import com.kkokkomu.short_news.type.ECategory;
import com.kkokkomu.short_news.type.EHomeFilter;
import com.kkokkomu.short_news.type.ENewsReaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final UserRepository userRepository;
    private final NewsReactionRepository newsReactionRepository;

    private final NewsKeywordService newsKeywordService;

    /* 홈화면 */
    public GenerateNewsDto generateNews() {
        News news = News.builder().build();

        news = newsRepository.save(news);

        // 영상 생성 서버에서 영상 url 및 정보 받아옴

        String summary = "한 대표는 당 정책위의장의 사퇴 문제에 대해 인선은 당 대표의 권한이라고 밝혔습니다.\n 그는 인선 지연의 이유로 특수한 정국을 언급하며, 좋은 정치 수행을 위한 과정이라고 강조했습니다.\n 또한, 인물난 지적에 대해 반박하며 능력 있는 인재들이 많다고 자신감을 드러냈습니다.";
        List<String> keywords = new ArrayList<>(Arrays.asList("인선", "정책위", "변화"));
        String s3Url = "";
        String thumnailUrl = "";
        String title = "";
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
                title,
                summary,
                category
        );

        news = newsRepository.save(news);

        return GenerateNewsDto.builder()
                .newsDto(NewsDto.of(news))
                .keywords(newsKeywords.stream()
                        .map(newsKeyword -> newsKeyword.getKeyword().getKeyword())
                        .toList())
                .relatedUrl(relatedNews.getRelatedUrl())
                .build();
    } // 영상 생성 api

    public PagingResponseDto<List<NewsListDto>> readNewsList(Long userId, String category, EHomeFilter filter, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // 일단 최신순으로 조회
        Page<News> results = newsRepository.findAll(PageRequest.of(page, size));

        List<News> news = results.getContent();
        PageInfoDto pageInfo = PageInfoDto.fromPageInfo(results);

        List<NewsListDto> newsListDtos = new ArrayList<>();
        for (News newsItem : news) {

            // 뉴스 url 및 기본 정보
            NewsSummaryDto newsSummaryDto = NewsSummaryDto.of(newsItem);

            // 각 감정표현 별 갯수
            ReactionCntDto reactionCntDto = ReactionCntDto.builder()
                    .like(newsReactionRepository.countByNewsIdAndReaction(newsItem.getId(), ENewsReaction.LIKE))
                    .angry(newsReactionRepository.countByNewsIdAndReaction(newsItem.getId(), ENewsReaction.ANGRY))
                    .sad(newsReactionRepository.countByNewsIdAndReaction(newsItem.getId(), ENewsReaction.SAD))
                    .surprise(newsReactionRepository.countByNewsIdAndReaction(newsItem.getId(), ENewsReaction.SURPRISE))
                    .build();

            // 유저 감정표현 여부
            NewReactionByUserDto newReactionByUserDto = NewReactionByUserDto.builder()
                    .like(newsReactionRepository.existsByNewsIdAndUserIdAndReaction(newsItem.getId(), userId, ENewsReaction.LIKE))
                    .angry(newsReactionRepository.existsByNewsIdAndUserIdAndReaction(newsItem.getId(), userId, ENewsReaction.ANGRY))
                    .sad(newsReactionRepository.existsByNewsIdAndUserIdAndReaction(newsItem.getId(), userId, ENewsReaction.SAD))
                    .surprise(newsReactionRepository.existsByNewsIdAndUserIdAndReaction(newsItem.getId(), userId, ENewsReaction.SURPRISE))
                    .build();

            // dto 생성
            newsListDtos.add(
                    NewsListDto.builder()
                            .shortformList(newsSummaryDto)
                            .reactionCnt(reactionCntDto)
                            .userReaction(newReactionByUserDto)
                            .build()
            );
        }

        return PagingResponseDto.fromEntityAndPageInfo(newsListDtos, pageInfo);
    } // 숏폼 조회

    /* 검색화면 */


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
    }// 카테고리 enum casting
}
