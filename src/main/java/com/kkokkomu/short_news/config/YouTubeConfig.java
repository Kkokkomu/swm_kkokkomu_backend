package com.kkokkomu.short_news.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
@Slf4j
public class YouTubeConfig {
    @Value("classpath:youtube/kkokkomu-mvp-31a5934a9b06.json")
    private Resource serviceAccountKey;

    private static final String APPLICATION_NAME = "kkokkomu-mvp";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public YouTube getService() throws GeneralSecurityException, IOException {
        log.info("youtube get service");
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountKey.getInputStream())
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/youtube.upload"));
// Credential 내용 로그에 출력
        log.info("Credential: {}", credentials);

        return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
