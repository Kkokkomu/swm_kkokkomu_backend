package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.Reaction;
import com.kkokkomu.short_news.dto.reaction.response.ReactionCntDto;
import com.kkokkomu.short_news.dto.news.response.NewsDto;
import com.kkokkomu.short_news.dto.news.response.NewsWithReactionDto;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.ReactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final S3Service s3Service;
    private final ReactionRepository reactionRepository;
//    private final YoutubeService youtubeService;

    @Transactional // 있어야 Dynamic Update도 돼고, 다른 db간의 롤백도 보장
    public NewsDto uploadShortForm(MultipartFile file) {
        log.info("Service Upload short form");

        News news = News.builder()
                .shortformUrl("")
                .relatedUrl("www.naver.com")
                .youtubeUrl("wwww.youtube.com")
                .instagramUrl("www.instagram.com")
                .build();

        newsRepository.save(news);

        // s3에 숏폼 업로드
        String shrotFormUrl = s3Service.uploadShortNews(file, news.getId());

        // 유튜브 업로드(보류)
        // 유튜브 업로드 뻑나면 트렌젝션 롤백 되는지 확인해야댐
//        String youtubeUrl = youtubeService.uploadVideo(file, "test", "desc", "news", "public");
//        log.info("Video upload successful");

        news.updateShrotFormUrl(shrotFormUrl, "wwww.youtube.com");

        return NewsDto.fromEntity(news);
    } // 숏폼 업로드

    public List<NewsWithReactionDto> readShortForm(Long userId, int page, int size) {
        log.info("Service read short form");

        // 일단 size 맞춰서 뉴스 아무거나
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<News> news = newsRepository.findAll(PageRequest.of(page, size, sort)).getContent();

        //뉴스 리스트에서 뉴스 정보, 감정표현 개수 뽑아내기
        List<NewsWithReactionDto> newsWithReactionDtos = new ArrayList<>();
        for (News newsEntity : news) {
            // 뉴스 정보 뽑기
            NewsDto newsDto = NewsDto.fromEntity(newsEntity);

            // 감점표현 정보 뽑기
            List<Reaction> reactions = reactionRepository.findAllByNewsId(newsEntity.getId());

            Long great = 0L, hate = 0L, expect = 0L, surprise = 0L;
            for (Reaction reaction : reactions) {
                if(reaction.isGreat()) great++;
                else if(reaction.isHate()) hate++;
                else if(reaction.isExpect()) expect++;
                else if(reaction.isSurprise()) surprise++;
            }

            ReactionCntDto reactionCntDto = ReactionCntDto.builder()
                    .great(great)
                    .hate(hate)
                    .expect(expect)
                    .surprise(surprise)
                    .build();

            // 뉴스 정보, 감정표현 개수 합치기
            NewsWithReactionDto newsWithReactionDto = NewsWithReactionDto.builder()
                    .shortForm(newsDto)
                    .reaction(reactionCntDto)
                    .build();

            newsWithReactionDtos.add(newsWithReactionDto);
        }

        return newsWithReactionDtos;
    }

}
