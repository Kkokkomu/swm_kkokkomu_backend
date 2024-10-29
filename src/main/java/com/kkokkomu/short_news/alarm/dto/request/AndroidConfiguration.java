package com.kkokkomu.short_news.alarm.dto.request;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AndroidConfiguration {

    public AndroidConfig androidConfig(String channelId) {
        return AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setSound("default")
                        .setChannelId(channelId)
                        .build())
                .build();
    }
}
