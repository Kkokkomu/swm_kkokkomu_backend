package com.kkokkomu.short_news.report.service;

import com.kkokkomu.short_news.report.repository.ReportedCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportedCommentService {
    private final ReportedCommentRepository reportedCommentRepository;
}
