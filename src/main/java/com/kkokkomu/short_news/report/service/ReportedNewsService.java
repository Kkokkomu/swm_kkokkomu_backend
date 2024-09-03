package com.kkokkomu.short_news.report.service;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.service.NewsLookupService;
import com.kkokkomu.short_news.report.domain.ReportedComment;
import com.kkokkomu.short_news.report.domain.ReportedNews;
import com.kkokkomu.short_news.report.dto.reportedComment.request.CreateReportedCommentDto;
import com.kkokkomu.short_news.report.dto.reportedComment.response.ReportedCommentDto;
import com.kkokkomu.short_news.report.dto.reportedNews.request.CreatedReportedNewsDto;
import com.kkokkomu.short_news.report.dto.reportedNews.response.ReportedNewsDto;
import com.kkokkomu.short_news.report.repository.ReportedNewsRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportedNewsService {
    private final ReportedNewsRepository reportedNewsRepository;

    private final UserLookupService userLookupService;
    private final NewsLookupService newsLookupService;

    public ReportedNewsDto create(CreatedReportedNewsDto createdReportedNewsDto, Long userId) {
        // 신고자, 신고뉴스 유효성 확인
        User reporter = userLookupService.findUserById(userId);

        News news = newsLookupService.findNewsById(createdReportedNewsDto.newsId());

        // 생성
        ReportedNews reportedNews = reportedNewsRepository.save(
                ReportedNews.builder()
                        .news(news)
                        .reporter(reporter)
                        .reason(createdReportedNewsDto.reason())
                        .build()
        );

        return ReportedNewsDto.of(reportedNews);
    }
}
