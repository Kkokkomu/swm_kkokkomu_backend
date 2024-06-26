package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final S3Service s3Service;

    public String uploadShortForm(MultipartFile file){
        return "success";
    }
}
