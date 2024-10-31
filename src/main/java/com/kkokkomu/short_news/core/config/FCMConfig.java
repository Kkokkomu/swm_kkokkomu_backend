package com.kkokkomu.short_news.core.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FCMConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        // ClassPathResource를 사용하여 클래스패스에서 firebase_key.json 파일을 로드
        ClassPathResource resource = new ClassPathResource("firebase/firebase_service_key.json");

        // 리소스가 존재하는지 확인
        if (!resource.exists()) {
            throw new IllegalStateException("Firebase key file not found in classpath: firebase/firebase_key.json");
        }

        try (InputStream refreshToken = resource.getInputStream()) {
            // Firebase 앱 인스턴스가 이미 있는지 확인
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            FirebaseApp firebaseApp;

            if (!firebaseApps.isEmpty()) {
                // 기존 인스턴스가 있으면 재사용
                firebaseApp = FirebaseApp.getInstance();
            } else {
                // 새 Firebase 앱 인스턴스 초기화
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(refreshToken))
                        .build();

                firebaseApp = FirebaseApp.initializeApp(options);
            }

            // FirebaseMessaging 인스턴스 반환
            return FirebaseMessaging.getInstance(firebaseApp);
        }
    }
}