package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.NewsKeyword;
import com.kkokkomu.short_news.domain.RelatedNews;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.common.PageInfoDto;
import com.kkokkomu.short_news.dto.common.PagingResponseDto;
import com.kkokkomu.short_news.dto.news.response.*;
import com.kkokkomu.short_news.dto.newsReaction.response.NewReactionByUserDto;
import com.kkokkomu.short_news.dto.newsReaction.response.ReactionCntDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.*;
import com.kkokkomu.short_news.type.ECategory;
import com.kkokkomu.short_news.type.EHomeFilter;
import com.kkokkomu.short_news.type.ENewsReaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final NewsKeywordRepository newsKeywordRepository;

    private final NewsKeywordService newsKeywordService;

    /* 홈화면 */

    @jakarta.transaction.Transactional
    public GenerateNewsDto generateNews() {
        News news = News.builder().build();

        news = newsRepository.save(news);

        // 영상 생성 서버에서 영상 url 및 정보 받아옴

        String summary = "박찬대 더불어민주당 원내대표가 경제 비상상황에 대한 초당적 대처를 위해 여·야 영수회담을 조속히 개최할 것을 제안했습니다. 그는 정부와 국회 간의 상시 정책협의기구 구축의 필요성과 윤석열 대통령의 재의요구권 행사 중단도 요구했습니다. 박 원내대표는 현재의 경제 위기가 민생에 중대한 영향을 미칠 수 있음을 강조하며 협력의 중요성을 역설했습니다.";
        List<String> keywords = new ArrayList<>(Arrays.asList("여·야 회담", "정책협의기구", "경제 위기"));
        String s3Url = "https://kkm-shortform.s3.ap-northeast-2.amazonaws.com/6.mp4";
        String thumnailUrl = "";
        String title = "박찬대, 여·야 영수회담 제안 및 정책협의기구 구성 촉구";
        ECategory category = getCategoryByName("정치");
        String relatedUrl = "https://n.news.naver.com/mnews/article/028/0002701654";

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

    @jakarta.transaction.Transactional
    public GenerateNewsDto generateNews2() {
        News news = News.builder().build();

        news = newsRepository.save(news);

        // 영상 생성 서버에서 영상 url 및 정보 받아옴

        String summary = "배우 한지민이 밴드 잔나비 보컬 최정훈과 열애 중임을 공식적으로 발표했습니다. 소속사는 두 사람이 최근 연인 관계로 발전했으며, 팬들의 응원을 요청했습니다. 한지민과 최정훈은 지난해 방송 프로그램을 통해 인연을 쌓았던 것으로 알려졌습니다.";
        List<String> keywords = new ArrayList<>(Arrays.asList("한지민", "최정훈", "열애"));
        String s3Url = "https://kkm-shortform.s3.ap-northeast-2.amazonaws.com/1.mp4";
        String thumnailUrl = "";
        String title = "한지민, 최정훈과 열애 인정";
        ECategory category = getCategoryByName("연애");
        String relatedUrl = "https://m.entertain.naver.com/ranking/article/609/0000883902";

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
        Page<News> results = newsRepository.findAllCreatedAtDesc(PageRequest.of(page, size));

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
    } // 숏폼 리스트 조회

    public PagingResponseDto<List<GuestNewsListDto>> guestReadNewsList(int page, int size) {
        // 일단 최신순으로 조회
        Page<News> results = newsRepository.findAllCreatedAtDesc(PageRequest.of(page, size));

        List<News> news = results.getContent();
        PageInfoDto pageInfo = PageInfoDto.fromPageInfo(results);

        List<GuestNewsListDto> newsListDtos = new ArrayList<>();
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

            // dto 생성
            newsListDtos.add(
                    GuestNewsListDto.builder()
                            .shortformList(newsSummaryDto)
                            .reactionCnt(reactionCntDto)
                            .build()
            );
        }

        return PagingResponseDto.fromEntityAndPageInfo(newsListDtos, pageInfo);
    } // 비로그인 숏폼 리스트 조회

    @Transactional
    public NewsInfoDto readNewsInfo(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));

        // 키워드
        List<String> keywords = newsKeywordRepository.findAllByNewsId(newsId)
                .stream()
                .map(keyword -> keyword.getKeyword().getKeyword())
                .toList();

        return NewsInfoDto.builder()
                .news(NewsDto.of(news))
                .keywords(keywords)
                .build();
    } // 뉴스 정보 조회

    /* 검색화면 */
    public List<SearchNewsDto> getCategoryFilteredNews(String category, Long cursorId, int size) {

        log.info("getfilteredNews service");

        ECategory eCategory = ECategory.valueOf(category.toUpperCase());

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<News> news;
        if (cursorId == null) {
            news = newsRepository.findFirstPageByCategoryOrderByIdDesc(eCategory, pageRequest);
        } else {
            news = newsRepository.findByCategoryAndIdLessThanOrderByIdDesc(eCategory, cursorId, pageRequest);
        }

        return SearchNewsDto.of(news);
    } // 탐색 화면 카테고리 필터 조회

    // 뉴스 인기순 조회

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
