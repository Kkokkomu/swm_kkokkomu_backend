package com.kkokkomu.short_news.news.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.dto.PageInfoDto;
import com.kkokkomu.short_news.core.dto.PagingResponseDto;
import com.kkokkomu.short_news.keyword.domain.NewsKeyword;
import com.kkokkomu.short_news.news.dto.news.request.CreateGenerateNewsDto;
import com.kkokkomu.short_news.news.dto.news.request.RequestGenerateNewsDto;
import com.kkokkomu.short_news.news.dto.news.response.*;
import com.kkokkomu.short_news.news.dto.newsReaction.response.NewReactionByUserDto;
import com.kkokkomu.short_news.news.dto.newsReaction.response.ReactionCntDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.repository.NewsRepository;
import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.core.type.EHomeFilter;
import com.kkokkomu.short_news.core.util.CategoryUtil;
import com.kkokkomu.short_news.keyword.service.NewsKeywordService;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserService;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.kkokkomu.short_news.core.constant.Constant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService{
    private final NewsRepository newsRepository;

    private final UserService userService;
    private final NewsKeywordService newsKeywordService;
    private final NewsReactionService newsReactionService;
    private final NewsLookupService newsLookupService;

    private final CategoryUtil categoryUtil;

    /* 홈화면 */

    public PagingResponseDto<List<NewsListDto>> readNewsList(Long userId, String category, EHomeFilter filter, int page, int size) {
        User user = userService.findUserById(userId);

        // 일단 최신순으로 조회
        Page<News> results = newsRepository.findAllCreatedAtDesc(PageRequest.of(page, size));

        List<News> news = results.getContent();
        PageInfoDto pageInfo = PageInfoDto.fromPageInfo(results);

        List<NewsListDto> newsListDtos = new ArrayList<>();
        for (News newsItem : news) {

            // 뉴스 url 및 기본 정보
            NewsSummaryDto newsSummaryDto = NewsSummaryDto.of(newsItem);

            // 각 감정표현 별 갯수
            ReactionCntDto reactionCntDto = newsReactionService.countNewsReaction(newsItem.getId());

            // 유저 감정표현 여부
            NewReactionByUserDto newReactionByUserDto = newsReactionService.checkNewsReaction(userId, newsItem.getId());

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
            ReactionCntDto reactionCntDto = newsReactionService.countNewsReaction(newsItem.getId());

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
        News news = newsLookupService.findNewsById(newsId);

        // 키워드
        List<String> keywords = newsKeywordService.getStrKeywordListByNewsId(newsId);

        return NewsInfoDto.builder()
                .news(NewsDto.of(news))
                .keywords(keywords)
                .build();
    } // 뉴스 정보 조회

    /* 탐색화면 */
    public CursorResponseDto<List<SearchNewsDto>> getLatestNewsFilteredByCategory(String category, Long cursorId, int size) {

        log.info("getfilteredNews service");

        ECategory eCategory = ECategory.valueOf(category.toUpperCase());

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            results = newsRepository.findFirstPageByCategoryOrderByIdDesc(eCategory, pageRequest);
        } else {
            results = newsRepository.findByCategoryAndIdLessThanOrderByIdDesc(eCategory, cursorId, pageRequest);
        }
        news = results.getContent();

        List<SearchNewsDto> searchNewsDtos = SearchNewsDto.of(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(searchNewsDtos, cursorInfoDto);
    } // 탐색 화면 카테고리 필터 최신순

    public CursorResponseDto<List<SearchNewsDto>> getPopularNewsFilteredByCategory(Long cursorId, int size) {

        log.info("getPopularNewsFilteredByCategory service");

        // 커서 아이디에 해당하는 뉴스가 있는지 검사
        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            results = newsRepository.findFirstPageByPopularity(VIEW_WEIGHT, COMMENT_WEIGHT, REACTION_WEIGHT, SHARE_WEIGHT, DATE_WEIGHT, pageRequest);
        } else {
            // cursorScore 계산
            News cursorNews = newsLookupService.findNewsById(cursorId);

            double cursorScore = calculateScore(cursorNews);

            results = newsRepository.findByPopularityLessThan(VIEW_WEIGHT, COMMENT_WEIGHT, REACTION_WEIGHT, SHARE_WEIGHT, DATE_WEIGHT, cursorScore, cursorId, pageRequest);
        }
        news = results.getContent();

        List<SearchNewsDto> searchNewsDtos = SearchNewsDto.of(news);
        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(searchNewsDtos, cursorInfoDto);
    } // 탐색 화면 카테고리 필터 인기순

    public CursorResponseDto<List<SearchNewsDto>> searchLatestNews(String category, String text, Long cursorId, int size) {
        log.info("searchLatestNews service");

        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<ECategory> categoryList = categoryUtil.getCategoryList(category);

        List<News> news;
        Page<News> results;
        if (cursorId == null) { // 첫 요청
            results = newsRepository.findFirstByKeywordOrderByIdDesc(categoryList, text, pageRequest);
        } else { // 두 번째 이후 요청
            if (newsRepository.existsById(cursorId)){ // 커서 id에 해당하는 뉴스가 있는지 검사
                    throw new CommonException(ErrorCode.NOT_FOUND_NEWS);
            }

            results = newsRepository.findByKeywordOrderByIdDesc(categoryList, cursorId, text, pageRequest);
        }
        news = results.getContent();

        List<SearchNewsDto> newsDtos = SearchNewsDto.of(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsDtos, cursorInfoDto);
    } // 최신순 뉴스 검색

    @Transactional(readOnly = true)
    public CursorResponseDto<List<SearchNewsDto>> searchPopularNews(String category, String text, Long cursorId, int size) {
        log.info("searchPopularNews service");

        if (cursorId != null && !newsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        PageRequest pageRequest = PageRequest.of(0, size);

        List<String> categoryList = categoryUtil.getCategoryStringList(category);

        List<News> news;
        Page<News> results;
        if (cursorId == null) {
            results = newsRepository.findFirstByKeywordOrderByPopularity(categoryList, VIEW_WEIGHT, COMMENT_WEIGHT, REACTION_WEIGHT, SHARE_WEIGHT, DATE_WEIGHT, text, pageRequest);
        } else {
            // cursorScore 계산
            News cursorNews = newsLookupService.findNewsById(cursorId);

            double cursorScore = calculateScore(cursorNews);

            results = newsRepository.findByKeywordOrderByPopularity(categoryList, VIEW_WEIGHT, COMMENT_WEIGHT, REACTION_WEIGHT, SHARE_WEIGHT, DATE_WEIGHT, cursorScore, text, pageRequest);
        }
        news = results.getContent();

        List<SearchNewsDto> newsDtos = SearchNewsDto.of(news);

        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(newsDtos, cursorInfoDto);
    } // 인기순 뉴스 검색

    /* 관리자 */

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
        log.info("Sending POST request to URL: {}", url);
        log.info("Request Headers: {}", headers);  // 헤더 로그 추가
        ResponseEntity<GenerateResponseDto[]> response;
        try {
            // 요청 본문을 JSON으로 변환해 기록
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(requestGenerateNewsDto);
            log.info("Request payload as JSON: {}", jsonPayload);

            response = restTemplate.postForEntity(url, entity, GenerateResponseDto[].class);
            log.info("Received response with status code: {}", response.getStatusCode());
            log.info("Response data length: {}", Objects.requireNonNull(response.getBody()).length);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred: Status code: {}, Response body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            log.error("Stack trace: ", e);  // 스택 트레이스 전체 기록
            throw e;
        } catch (RestClientException e) {
            log.error("Rest client error occurred: {}", e.getMessage());
            log.error("Stack trace: ", e);  // 스택 트레이스 전체 기록
            throw e;
        } catch (JsonProcessingException e) {
            log.error("Error serializing request payload to JSON: {}", e.getMessage());
            log.error("Stack trace: ", e);  // 스택 트레이스 전체 기록
            throw new RuntimeException("Error processing JSON", e);
        }

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
            log.info("section : {}", dataDto.section());
            ECategory category = categoryUtil.getCategoryByName(dataDto.section());
            String relatedUrl = dataDto.url();

            // 뉴스 키워드 생성
            List<NewsKeyword> newsKeywords = newsKeywordService.registerNewsKeyword(news, keywords);

            news.update(
                    s3Url,
                    "",
                    "",
                    thumnailUrl,
                    relatedUrl,
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
                            .build()
            );
        }

        return generateNewsDtos;
    } // 영상 생성 api

    // cursorScore 계산 메서드
    private double calculateScore(News news) {
        long daysDifference = ChronoUnit.DAYS.between(news.getCreatedAt(), LocalDateTime.now());

        double score = (news.getViewCnt() * VIEW_WEIGHT) +
                (news.getComments().size() * COMMENT_WEIGHT) +
                (news.getReactions().size() * REACTION_WEIGHT) +
                (news.getSharedCnt() * SHARE_WEIGHT) +
                (daysDifference * DATE_WEIGHT);

        return score;
    }
}
