package com.kkokkomu.short_news.core.config.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client s3Client;

//    @Value("${cloud.aws.s3.short-news}")
//    private String bucketShortNews;

    @Value("${cloud.aws.s3.user-profile}")
    private String bucketUserProfile;

    public String putUserProfile(MultipartFile file, Long userId) {
        log.info("upload UserProfile start");

        try{
            // 파일 이름에서 확장자 추출
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex != -1 && dotIndex < originalFileName.length() - 1) {
                fileExtension = originalFileName.substring(dotIndex);
            }

            String fileName = userId + fileExtension;
            log.info("upload " + fileName);

            // 파일 메타데이터 생성
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.putObject(new PutObjectRequest(bucketUserProfile, fileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return s3Client.getUrl(bucketUserProfile, fileName).toString();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CommonException(ErrorCode.S3_PROCESSING_ERROR);
        }
    } // 프로필 이미지 업로드

    public void deleteUserProfileByUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String s3Key = url.getPath().substring(1); // URL에서 첫 '/'를 제거하여 S3 객체 키 추출
            String bucketName = url.getHost().split("\\.")[0]; // 호스트명에서 첫 부분이 버킷 이름

            s3Client.deleteObject(bucketName, s3Key);
            log.info("Deleted " + s3Key + " from S3 bucket: " + bucketName);

        } catch (MalformedURLException e) {
            log.error("Invalid URL provided", e);
            throw new CommonException(ErrorCode.S3_PROCESSING_ERROR);
        } catch (AmazonServiceException e) {
            log.error("Error occurred while deleting the file from S3", e);
            throw new CommonException(ErrorCode.S3_PROCESSING_ERROR);
        }
    }
    // 프로필 이미지 삭제

}
