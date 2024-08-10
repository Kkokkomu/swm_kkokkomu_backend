package com.kkokkomu.short_news.scheduler;

import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.news.request.CreateGenerateNewsDto;
import com.kkokkomu.short_news.dto.news.response.GenerateNewsDto;
import com.kkokkomu.short_news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewsScheduler {
    private final NewsService newsService;

    @Scheduled(cron = "0 0 8 * * *") // 매일 아침 8시
    public void generateNewsAt8AM() {
        // 필요 시 CreateGenerateNewsDto 객체를 만들어서 요청 본문에 넣을 수 있습니다.
        CreateGenerateNewsDto createGenerateNewsDto = CreateGenerateNewsDto.builder()
                .count_news(2)
                .count_entertain(2)
                .count_sports(1)
                .build();

        List<GenerateNewsDto> generateNewsDtos = newsService.generateNews(createGenerateNewsDto);
        log.info("generateNewsDtos: {}", generateNewsDtos);
    }
}
