package com.kkokkomu.short_news.core.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FCMConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        // 1. firebase_key.json을 ClassPath에서 로드
        ClassPathResource resource = new ClassPathResource("firebase/firebase_service_key.json");

        try (InputStream refreshToken = resource.getInputStream()) {
            // 2. Firebase 앱 인스턴스가 이미 있는지 확인
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            FirebaseApp firebaseApp;

            if (firebaseApps != null && !firebaseApps.isEmpty()) {
                // 기존 인스턴스가 있으면 재사용
                firebaseApp = FirebaseApp.getInstance();
            } else {
                // 3. 새 Firebase 앱 인스턴스 초기화
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(refreshToken))
                        .build();

                firebaseApp = FirebaseApp.initializeApp(options);
            }

            // 4. FirebaseMessaging 인스턴스 반환
            return FirebaseMessaging.getInstance(firebaseApp);
        }
    }
}