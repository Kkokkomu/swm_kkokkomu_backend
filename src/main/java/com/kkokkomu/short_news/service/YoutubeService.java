package com.kkokkomu.short_news.service;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.kkokkomu.short_news.config.YouTubeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeService {
    private final YouTubeConfig youtubeClient;
    private static final String VIDEO_FILE_FORMAT = "video/*";
    private static final int MAX_RETRY_ATTEMPTS = 1; // 최대 재시도 횟수

    public String uploadVideo(MultipartFile videoFile, String title, String description, String tags, String privacyStatus) {
        log.info("Uploading youtube video");
        int retryAttempts = 0;

        while (retryAttempts <= MAX_RETRY_ATTEMPTS) {
            try {
                YouTube youtubeService = youtubeClient.getService();

                Video videoObjectDefiningMetadata = new Video();

                VideoStatus status = new VideoStatus();
                status.setPrivacyStatus(privacyStatus);
                videoObjectDefiningMetadata.setStatus(status);

                VideoSnippet snippet = new VideoSnippet();
                snippet.setTitle(title);
                snippet.setDescription(description);
                snippet.setTags(Collections.singletonList(tags));
                videoObjectDefiningMetadata.setSnippet(snippet);

                AbstractInputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT, videoFile.getInputStream());

                YouTube.Videos.Insert videoInsert = youtubeService.videos()
                        .insert(Arrays.asList("snippet", "statistics", "status"), videoObjectDefiningMetadata, mediaContent);

                Video returnedVideo = videoInsert.execute();
                String videoId = returnedVideo.getId();
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                log.info("Video upload completed: {}", videoUrl);

                return videoUrl;
            } catch (IOException e) {
                log.error("IOException during video upload attempt {}: {}", retryAttempts + 1, e.getMessage());
                retryAttempts++;
                if (retryAttempts <= MAX_RETRY_ATTEMPTS) {
                    log.info("Retrying upload attempt {}...", retryAttempts);
                } else {
                    throw new RuntimeException("Failed to upload video to YouTube after maximum retry attempts", e);
                }
            } catch (Exception e) {
                log.error("Error uploading video: {}", e.getMessage());
                throw new RuntimeException("Failed to upload video to YouTube", e);
            }
        }

        throw new RuntimeException("Failed to upload video to YouTube after maximum retry attempts");
    }
}
