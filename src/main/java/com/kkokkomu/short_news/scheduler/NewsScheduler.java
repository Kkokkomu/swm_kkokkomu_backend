package com.kkokkomu.short_news.scheduler;

import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.news.request.CreateGenerateNewsDto;
import com.kkokkomu.short_news.dto.news.response.GenerateNewsDto;
import com.kkokkomu.short_news.service.MailService;
import com.kkokkomu.short_news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewsScheduler {
    private final NewsService newsService;
    private final MailService mailService;

    @Scheduled(cron = "0 0 8 * * *") // 매일 아침 8시
    public void generateNewsAt8AM() {
        // 필요 시 CreateGenerateNewsDto 객체를 만들어서 요청 본문에 넣을 수 있습니다.
        CreateGenerateNewsDto createGenerateNewsDto = CreateGenerateNewsDto.builder()
                .count_news(2)
                .count_entertain(2)
                .count_sports(2)
                .build();

        List<GenerateNewsDto> generateNewsDtos = newsService.generateNews(createGenerateNewsDto);

        log.info("generateNewsDtos: {}", generateNewsDtos);

        // 이메일 내용 생성
        StringBuilder content = new StringBuilder();
        for (GenerateNewsDto generateNewsDto : generateNewsDtos) {
            if (generateNewsDto.newsDto() != null) {
                content.append("<p> Title: ").append(generateNewsDto.newsDto().title()).append("</p>");
                content.append("<p> URL: ").append(generateNewsDto.newsDto().shortformUrl()).append("</p>");
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
        mailService.sendEmail("aahhll654@gmail.com", LocalDate.now().toString() + " kkm 뉴스", content.toString());
        log.info("send gouyeonch@naver.com");
        mailService.sendEmail("gouyeonch@naver.com", LocalDate.now().toString() + " kkm 뉴스", content.toString());
        log.info("send leesk9663@gmail.com");
        mailService.sendEmail("leesk9663@gmail.com", LocalDate.now().toString() + " kkm 뉴스", content.toString());
        
    }
}
