package com.kkokkomu.short_news.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.v1-news}")
    private String bucketShortNews;

    public String uploadShortNews(MultipartFile file, Long newsId) throws IOException {
        String fileName = newsId + "_" + file.getOriginalFilename();

        // 파일 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        s3Client.putObject(new PutObjectRequest(bucketShortNews, fileName, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return s3Client.getUrl(bucketShortNews, fileName).toString();
    }
}
