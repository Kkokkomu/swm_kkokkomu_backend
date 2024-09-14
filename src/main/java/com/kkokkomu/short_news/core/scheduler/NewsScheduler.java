package com.kkokkomu.short_news.core.scheduler;

import com.kkokkomu.short_news.core.config.service.RedisService;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.dto.news.request.CreateGenerateNewsDto;
import com.kkokkomu.short_news.news.dto.news.response.GenerateNewsDto;
import com.kkokkomu.short_news.core.config.service.MailService;
import com.kkokkomu.short_news.news.service.AdminNewsService;
import com.kkokkomu.short_news.news.service.HomeNewsService;
import com.kkokkomu.short_news.news.service.NewsViewHistService;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewsScheduler {
    private final AdminNewsService adminNewsService;
    private final HomeNewsService homeNewsService;
    private final MailService mailService;
    private final UserLookupService userLookupService;
    private final NewsViewHistService newsViewHistService;

    @Scheduled(cron = "0 0 8 * * *") // 매일 아침 8시
    public void generateNewsAt8AM() {
        // 필요 시 CreateGenerateNewsDto 객체를 만들어서 요청 본문에 넣을 수 있습니다.
        CreateGenerateNewsDto createGenerateNewsDto = CreateGenerateNewsDto.builder()
                .count_news(2)
                .count_entertain(2)
                .count_sports(2)
                .build();

        List<GenerateNewsDto> generateNewsDtos = adminNewsService.generateNews(createGenerateNewsDto);

        log.info("generateNewsDtos: {}", generateNewsDtos);

        // 이메일 내용 생성
        StringBuilder content = new StringBuilder();
        for (GenerateNewsDto generateNewsDto : generateNewsDtos) {
            if (generateNewsDto.newsDto() != null) {
                content.append("<p> Title: ").append(generateNewsDto.newsDto().title()).append("</p>");
                content.append("<p> URL: ").append(generateNewsDto.newsDto().shortformUrl()).append("</p>");
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
        mailService.sendEmail("aahhll654@gmail.com", LocalDate.now().toString() + " kkm 뉴스", content.toString());
        log.info("send gouyeonch@naver.com");
        mailService.sendEmail("gouyeonch@naver.com", LocalDate.now().toString() + " kkm 뉴스", content.toString());
        log.info("send leesk9663@gmail.com");
        mailService.sendEmail("leesk9663@gmail.com", LocalDate.now().toString() + " kkm 뉴스", content.toString());
        
    } // 뉴스 생성

    @Scheduled(fixedRate = 600000) // 10분 마다
    public void syncViewCountToDatabase() {
        log.info("syncViewCountToDatabase");
        homeNewsService.updateViewCnt();
    } // 뉴스 조회수 동기화

    @Scheduled(cron = "0 0 4 * * ?") // 매일 새벽 4시에 실행
    public void syncAllUsersViewHistory() {
        log.info("syncAllUsersViewHistory");
        List<User> users = userLookupService.findAll();
        for (User user : users) {
            newsViewHistService.updateNewsHist(user.getId());
        }
    } // 모든 유저에 대해 시청기록 동기화
}
