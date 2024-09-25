package com.kkokkomu.short_news.news.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkokkomu.short_news.core.config.service.RedisService;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.ECategory;
import com.kkokkomu.short_news.core.util.CategoryUtil;
import com.kkokkomu.short_news.keyword.domain.NewsKeyword;
import com.kkokkomu.short_news.keyword.service.NewsKeywordService;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.dto.news.request.CreateGenerateNewsDto;
import com.kkokkomu.short_news.news.dto.news.request.RequestGenerateNewsDto;
import com.kkokkomu.short_news.news.dto.news.request.UpdateNewsDto;
import com.kkokkomu.short_news.news.dto.news.response.*;
import com.kkokkomu.short_news.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.kkokkomu.short_news.core.constant.Constant.VIDEO_SERVER_GENERATE_HOST;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNewsService {
    private final NewsRepository newsRepository;

    private final NewsKeywordService newsKeywordService;

    private final CategoryUtil categoryUtil;
    private final NewsLookupService newsLookupService;
    private final RedisService redisService;

    /* 관리자 */
    @jakarta.transaction.Transactional
    public List<GenerateNewsDto> generateNewsList(CreateGenerateNewsDto createGenerateNewsDto) {
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

        log.info("response data length: {}", Objects.requireNonNull(response.getBody()).length);
        log.info("response data : {}", (Object) Objects.requireNonNull(response.getBody()));
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
            String thumbnailUrl = generateResponseDto.thumbnail();
            log.info("thumbnailUrl : {}", generateResponseDto.thumbnail());
            String title = dataDto.title();
            log.info("data : {}", dataDto);
            log.info("section : {}", dataDto.section());
            ECategory category = categoryUtil.getCategoryByName(dataDto.section());
            String relatedUrl = dataDto.url();
            log.info("relatedUrl : {}", dataDto.url());

            // 뉴스 키워드 생성
            List<NewsKeyword> newsKeywords = newsKeywordService.registerNewsKeyword(news, keywords);

            news.update(
                    s3Url,
                    "",
                    "",
                    relatedUrl,
                    thumbnailUrl,
                    title,
                    summary,
                    category
            );

            // 레디스 랭킹 초기화
            redisService.normalizeScores();

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
    } // 영상 리스트 생성 api

    @jakarta.transaction.Transactional
    public List<GenerateNewsDto> generateNews(CreateGenerateNewsDto createGenerateNewsDto) {
        // 임시 뉴스 객체 생성 및 id 추출
        // 요청을 위해 리스트 형식으로 저장
        List<News> newsList = new ArrayList<>();

        newsList.add(newsRepository.save(News.builder().build()));

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

        log.info("response data length: {}", Objects.requireNonNull(response.getBody()).length);
        log.info("response data : {}", (Object) Objects.requireNonNull(response.getBody()));
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
            String thumbnailUrl = generateResponseDto.thumbnail();
            log.info("thumbnailUrl : {}", generateResponseDto.thumbnail());
            String title = dataDto.title();
            log.info("data : {}", dataDto);
            log.info("section : {}", dataDto.section());
            ECategory category = categoryUtil.getCategoryByName(dataDto.section());
            String relatedUrl = dataDto.url();
            log.info("relatedUrl : {}", dataDto.url());

            // 뉴스 키워드 생성
            List<NewsKeyword> newsKeywords = newsKeywordService.registerNewsKeyword(news, keywords);

            news.update(
                    s3Url,
                    "",
                    "",
                    relatedUrl,
                    thumbnailUrl,
                    title,
                    summary,
                    category
            );

            // 레디스 랭킹 초기화
            redisService.normalizeScores();

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
    } // 영상 리스트 생성 api

    public NewsDto updateNews(UpdateNewsDto updateNewsDto) {
        News news = newsLookupService.findNewsById(updateNewsDto.id());

        news.update(
                updateNewsDto.shortformUrl() != null ? updateNewsDto.shortformUrl() : news.getShortformUrl(),
                updateNewsDto.youtubeUrl() != null ? updateNewsDto.youtubeUrl() : news.getYoutubeUrl(),
                updateNewsDto.instagramUrl() != null ? updateNewsDto.instagramUrl() : news.getInstagramUrl(),
                updateNewsDto.relatedUrl() != null ? updateNewsDto.relatedUrl() : news.getRelatedUrl(),
                updateNewsDto.thumbnail() != null ? updateNewsDto.thumbnail() : news.getThumbnail(),
                updateNewsDto.title() != null ? updateNewsDto.title() : news.getTitle(),
                updateNewsDto.summary() != null ? updateNewsDto.summary() : news.getSummary(),
                updateNewsDto.category() != null ? updateNewsDto.category() : news.getCategory()
        );

        news = newsRepository.save(news);

        return NewsDto.of(news);
    } // 뉴스 수정

    // 영상 처리 내역 조회
}
