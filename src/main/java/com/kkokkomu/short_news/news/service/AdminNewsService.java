package com.kkokkomu.short_news.news.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkokkomu.short_news.core.config.service.MailService;
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
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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
    private final MailService mailService;

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

        // 랭킹 초기화
        log.info("news ranking rese");
        News topNews = newsRepository.findTopByOrderByScoreDesc();
        Double topScore = topNews.getScore() * -1;
        Double reseScore = topScore - 10;

        List<News> newsListAll = newsRepository.findAll();
        for (News news : newsListAll) {
            news.addScore(reseScore);
        }
        newsRepository.saveAll(newsListAll);

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

            // 랭키보드 등록
            log.info("apply redis {}", news.getId());
            redisService.applyRankingByGenerate(news);

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

        // 랭킹 초기화
        log.info("news ranking rese");
        News topNews = newsRepository.findTopByOrderByScoreDesc();
        log.info("top news {}", topNews.getId());
        Double topScore = topNews.getScore() * -1;
        Double reseScore = topScore - 10;

        List<News> newsListAll = newsRepository.findAll();
        for (News news : newsListAll) {
            news.addScore(reseScore);
        }
        newsRepository.saveAll(newsListAll);

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

            news = newsRepository.save(news);

            // 랭키보드 등록
            log.info("apply redis {}", news.getId());
            redisService.applyRankingByGenerate(news);

            generateNewsDtos.add(
                    GenerateNewsDto.builder()
                            .newsDto(NewsDto.of(news))
                            .keywords(newsKeywords.stream()
                                    .map(newsKeyword -> newsKeyword.getKeyword().getKeyword())
                                    .toList())
                            .build()
            );
        }

        log.info("generateNewsDtos: {}", generateNewsDtos);

        // 이메일 내용 생성
        StringBuilder content = new StringBuilder();
        for (GenerateNewsDto generateNewsDto : generateNewsDtos) {
            if (generateNewsDto.newsDto() != null) {
                content.append("<p> Title: ").append(generateNewsDto.newsDto().title()).append("</p>");
                content.append("<p> URL: ").append(generateNewsDto.newsDto().shortformUrl().replace("kkm-shortform", "kkm-shortform-withad")).append("</p>");
                content.append("<p> origin: ").append(generateNewsDto.newsDto().relatedUrl()).append("</p>");
            }

            if (generateNewsDto.keywords() != null && !generateNewsDto.keywords().isEmpty()) {
                content.append("<p> Keywords: ").append(String.join(", ", generateNewsDto.keywords())).append("</p>");
            }

            content.append("</br></br>");
        }

        // 로그 출력 (디버깅 용도)
        log.info("Generated News Content: \n{}", content.toString());

        // 이메일 전송
        log.info("send aahhll654@gmail.com");
        mailService.sendNewsMail("aahhll654@gmail.com", LocalDate.now().toString() + " kkm 뉴스", content.toString());
        log.info("send gouyeonch@naver.com");
        mailService.sendNewsMail("gouyeonch@naver.com", LocalDate.now().toString() + " kkm 뉴스", content.toString());
        log.info("send leesk9663@gmail.com");
        mailService.sendNewsMail("leesk9663@gmail.com", LocalDate.now().toString() + " kkm 뉴스", content.toString());

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

    public void syncRanking() {
        log.info("syncRanking");
        Set<ZSetOperations.TypedTuple<String>> scores = redisService.getAllGlobalRank();
        for (ZSetOperations.TypedTuple<String> score : scores) {
            log.info("id : {}, score : {}", score.getValue(), score.getScore());
        }

        // 가져온 데이터를 처리하여 뉴스 엔티티의 점수를 업데이트
        if (scores != null && !scores.isEmpty()) {
            for (ZSetOperations.TypedTuple<String> score : scores) {
                String idString = score.getValue();
                Double rankScore = score.getScore();

                // String ID를 Long으로 변환하여 데이터베이스에서 해당 News 엔티티를 찾음
                Long newsId = Long.parseLong(idString);
                News news = newsRepository.findById(newsId)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NEWS));

                // 뉴스 엔티티의 점수를 업데이트
                log.info("be : {}, sync : {}", news.getScore(), rankScore);
                news.updateScore(news.getScore() + rankScore);
                newsRepository.save(news);
                log.info("af : {}", news.getScore());
            }
        }

        redisService.deleteGlobalRank();
    } // 레디스 글로벌 랭킹 db 동기화
}
