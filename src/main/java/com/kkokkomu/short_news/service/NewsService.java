package com.kkokkomu.short_news.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.NewsKeyword;
import com.kkokkomu.short_news.domain.RelatedNews;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.common.PageInfoDto;
import com.kkokkomu.short_news.dto.common.PagingResponseDto;
import com.kkokkomu.short_news.dto.news.request.CreateGenerateNewsDto;
import com.kkokkomu.short_news.dto.news.request.RequestGenerateNewsDto;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.kkokkomu.short_news.constant.Constant.VIDEO_SERVER_GENERATE_HOST;

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
    public List<GenerateNewsDto> generateNews(CreateGenerateNewsDto createGenerateNewsDto) {
        int repeat = createGenerateNewsDto.count_news() + createGenerateNewsDto.count_entertain() + createGenerateNewsDto.count_sports();

        // 임시 뉴스 객체 생성 및 id 추출
        List<News> newsList = new ArrayList<>();
        for (int i = 0; i < repeat; i++) {
            News news = News.builder().build();

            newsList.add(newsRepository.save(news));
        }
        List<Integer> idList = newsList.stream()
                .map(news -> Math.toIntExact(news.getId()))
                .toList();

        // 임시 생성 객체 id를 기반으로 한 요청 생성
        String url = VIDEO_SERVER_GENERATE_HOST;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestGenerateNewsDto requestGenerateNewsDto = RequestGenerateNewsDto.builder()
                .count_news(createGenerateNewsDto.count_news())
                .count_entertain(createGenerateNewsDto.count_entertain())
                .count_sports(createGenerateNewsDto.count_sports())
                .id_list(idList)
                .build();

        HttpEntity<RequestGenerateNewsDto> entity = new HttpEntity<>(requestGenerateNewsDto, headers);

        log.info("request video");
        ResponseEntity<GenerateResponseDto[]> response = restTemplate.postForEntity(url, entity, GenerateResponseDto[].class);

        log.info("response data : {}", Objects.requireNonNull(response.getBody()).length);
        GenerateResponseDto[] generateResponseDtos = response.getBody();

        // 영상 생성 서버에서 영상 url 및 정보 받아옴
        ObjectMapper objectMapper = new ObjectMapper();
        List<GenerateNewsDto> generateNewsDtos = new ArrayList<>();
        for (int i = 0; i < idList.size(); i++) {
            // 인덱스에 맞는 임시 뉴스 객체
            News news = newsList.get(i);

            // 인덱스에 맞는 비디오 서버 반환값
            GenerateResponseDto generateResponseDto;
            if (generateResponseDtos != null) {
                generateResponseDto = generateResponseDtos[i];
            } else {
                throw new CommonException(ErrorCode.VIDEO_SERVER_ERROR);
            }


            Map<String, Object> dataMap = generateResponseDto.data();
            NewsInfoDataDto dataDto = objectMapper.convertValue(dataMap, NewsInfoDataDto.class);

            NewsInfoSummaryDto summaryDto = dataDto.summary();
            Map<String, String> keywordMap = dataDto.keywords();


            String summary = summaryDto.sentence_total();
            List<String> keywords = new ArrayList<>(Arrays.asList(keywordMap.get("keyword_0"), keywordMap.get("keyword_1"), keywordMap.get("keyword_2")));
            String s3Url = generateResponseDto.s3();
            String thumnailUrl = "";
            String title = dataDto.title();
            ECategory category = getCategoryByName(dataDto.section());
            String relatedUrl = dataDto.url();

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

            generateNewsDtos.add(
                    GenerateNewsDto.builder()
                            .newsDto(NewsDto.of(news))
                            .keywords(newsKeywords.stream()
                                    .map(newsKeyword -> newsKeyword.getKeyword().getKeyword())
                                    .toList())
                            .relatedUrl(relatedNews.getRelatedUrl())
                            .build()
            );
        }

        return generateNewsDtos;
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

    @Transactional(readOnly = true)
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

//    public List<SearchNewsDto> getFilteredNewsByText(String category, String text, String order, Long cursorId, int size) {
//        log.info("getFilteredNewsByText service");
//
//
//    }
//    // 뉴스 인기순 조회

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
            category = ECategory.WORLD;
        } else if (Objects.equals(categoryName, "연예")) {
            category = ECategory.ENTERTAIN;
        } else if (Objects.equals(categoryName, "스포츠")) {
            category = ECategory.SPORTS;
        }

        return category;
    }// 카테고리 enum casting
}
