package com.kkokkomu.short_news.report.service;

import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.ECommentProgress;
import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.service.NewsLookupService;
import com.kkokkomu.short_news.report.domain.ReportedComment;
import com.kkokkomu.short_news.report.domain.ReportedNews;
import com.kkokkomu.short_news.report.dto.reportedComment.request.CreateReportedCommentDto;
import com.kkokkomu.short_news.report.dto.reportedComment.response.AdminCommentListDto;
import com.kkokkomu.short_news.report.dto.reportedComment.response.ReportedCommentDto;
import com.kkokkomu.short_news.report.dto.reportedNews.request.CreatedReportedNewsDto;
import com.kkokkomu.short_news.report.dto.reportedNews.response.AdminReportedNewsDto;
import com.kkokkomu.short_news.report.dto.reportedNews.response.ReportedNewsDto;
import com.kkokkomu.short_news.report.repository.ReportedNewsRepository;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public CursorResponseDto<List<AdminReportedNewsDto>> findAdminReportedNews(Long cursorId, int size) {
        // 커서에 해당하는 신고 내역이 존재하는지 검사
        if (cursorId != null && !reportedNewsRepository.existsById(cursorId)) {
            throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
        }

        // size에 따른 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(0, size);

        // 신고 리스트 조회
        Page<ReportedNews> results;
        if (cursorId == null) {
            // 처음 요청
            results = reportedNewsRepository.findFirstPageByProgressOrderByReportedAt(ECommentProgress.UNEXECUTED, pageRequest);
        } else {
            // 2번째부터
            results = reportedNewsRepository.findByProgressOrderByReportedAt(cursorId, ECommentProgress.UNEXECUTED, pageRequest);
        }
        List<ReportedNews> reportedNews = results.getContent();

        // 신고 관련 dto
        List<AdminReportedNewsDto> adminReportedNewsDtos = AdminReportedNewsDto.of(reportedNews);

        // 페이징 정보 dto
        CursorInfoDto cursorInfoDto = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(adminReportedNewsDtos, cursorInfoDto);
    }
}
