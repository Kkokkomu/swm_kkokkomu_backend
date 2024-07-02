package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.News;
import com.kkokkomu.short_news.domain.Reaction;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.common.PageInfoDto;
import com.kkokkomu.short_news.dto.common.PagingResponseDto;
import com.kkokkomu.short_news.dto.news.response.NewsDtoWithId;
import com.kkokkomu.short_news.dto.reaction.response.ReactionCntDto;
import com.kkokkomu.short_news.dto.news.response.NewsDto;
import com.kkokkomu.short_news.dto.news.response.NewsWithReactionDto;
import com.kkokkomu.short_news.dto.reaction.response.ReactionWithUser;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.NewsRepository;
import com.kkokkomu.short_news.repository.ReactionRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    private final UserRepository userRepository;
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

    @Transactional
    public PagingResponseDto readShortForm(String userId, int page, int size) {
        log.info("Service read short form");

        // 일단 size 맞춰서 뉴스 아무거나
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Page<News> newsPage = newsRepository.findAll(PageRequest.of(page, size, sort));

        List<News> news = newsPage.getContent();

        //뉴스 리스트에서 뉴스 정보, 감정표현 개수 뽑아내기
        List<NewsWithReactionDto> newsWithReactionDtos = new ArrayList<>();
        for (News newsEntity : news) {
            // 뉴스 정보 뽑기
            NewsDtoWithId newsDto = NewsDtoWithId.fromEntity(newsEntity);

            // 감점표현 정보 뽑기
            List<Reaction> reactions = reactionRepository.findAllByNewsId(newsEntity.getId());

            Long great = 0L, hate = 0L, expect = 0L, surprise = 0L;
            Boolean isGreat = null, isHate = null, isExpect = null, isSurprise = null;
            for (Reaction reaction : reactions) {
                if(reaction.isGreat()) great++;
                else if(reaction.isHate()) hate++;
                else if(reaction.isExpect()) expect++;
                else if(reaction.isSurprise()) surprise++;

                // 해당 뉴스에 유저가 이미 좋아요를 눌렀는지 체크
                if (reaction.getUser().getUuid() == userId){
                    if(reaction.isGreat()) isGreat = true;
                    else if(reaction.isHate()) isHate = true;
                    else if(reaction.isExpect()) isExpect = true;
                    else if(reaction.isSurprise()) isSurprise = true;
                }
            }

            ReactionCntDto reactionCntDto = ReactionCntDto.builder()
                    .great(great)
                    .hate(hate)
                    .expect(expect)
                    .surprise(surprise)
                    .build();

            ReactionWithUser reactionWithUser = ReactionWithUser.builder()
                    .great(isGreat)
                    .hate(isHate)
                    .expect(isExpect)
                    .surprise(isSurprise)
                    .build();

            // 뉴스 정보, 감정표현 개수 합치기
            NewsWithReactionDto newsWithReactionDto = NewsWithReactionDto.builder()
                    .shortForm(newsDto)
                    .reaction(reactionCntDto)
                    .reactionWithUser(reactionWithUser)
                    .build();

            newsWithReactionDtos.add(newsWithReactionDto);
        }

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(newsPage);

        return PagingResponseDto.fromEntityAndPageInfo(newsWithReactionDtos, pageInfoDto);
    }

}
