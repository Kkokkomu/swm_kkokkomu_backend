//package com.kkokkomu.short_news.scheduler;
//
//import com.kkokkomu.short_news.dto.common.ResponseDto;
//import com.kkokkomu.short_news.dto.news.request.CreateGenerateNewsDto;
//import com.kkokkomu.short_news.dto.news.response.GenerateNewsDto;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//
//@Component
//@Slf4j
//public class NewsScheduler {
//    @Scheduled(cron = "0 49 23 * * *") // 매일 아침 8시
//    public void generateNewsAt8AM() {
//        RestTemplate restTemplate = new RestTemplate();
//
//        String url = "http://localhost:8080/news/generate";
//
//        // 필요 시 CreateGenerateNewsDto 객체를 만들어서 요청 본문에 넣을 수 있습니다.
//        CreateGenerateNewsDto createGenerateNewsDto = CreateGenerateNewsDto.builder()
//                .count_news(1)
//                .count_entertain(0)
//                .count_sports(0)
//                .build();
//
//        // API 호출
//        ResponseEntity<ResponseDto<List<GenerateNewsDto>>> response = restTemplate.postForEntity(
//                url,
//                createGenerateNewsDto,
//                (Class<ResponseDto<List<GenerateNewsDto>>>) (Class<?>) ResponseDto.class
//        );
//
//        // 응답 처리
//        if (response.getStatusCode().is2xxSuccessful()) {
//            log.info("News generated successfully at 8 AM");
//        } else {
//            log.error("Failed to generate news at 8 AM: " + response.getStatusCode());
//        }
//    }
//}
