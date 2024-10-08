package com.kkokkomu.short_news.core.util;

import com.kkokkomu.short_news.core.constant.Constant;
import com.kkokkomu.short_news.core.oauth2.OAuth2UserInfo;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class OAuth2Util {
    private final RestTemplate restTemplate = new RestTemplate();

    public OAuth2UserInfo getKakaoUserInfo(String accessToken) {

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(Constant.AUTHORIZATION_HEADER, Constant.BEARER_PREFIX + accessToken);
        httpHeaders.add(Constant.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<?> kakaoProfileRequest = new HttpEntity<>(httpHeaders);
        log.info("kakaoProfileRequest" + kakaoProfileRequest);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Kakao API 요청에 실패했습니다.");
        }

        JsonElement element = JsonParser.parseString(response.getBody());

        log.info(element.toString());

        return OAuth2UserInfo.of(
                element.getAsJsonObject().get("id").getAsString(),
                element.getAsJsonObject().getAsJsonObject("kakao_account").get("email").getAsString()
        );
    }

    public OAuth2UserInfo getGoogleUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(Constant.AUTHORIZATION_HEADER, Constant.BEARER_PREFIX + accessToken);
        httpHeaders.add(Constant.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<?> googleProfileRequest = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                googleProfileRequest,
                String.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Google API 요청에 실패했습니다.");
        }

        JsonElement element = JsonParser.parseString(response.getBody());
        return OAuth2UserInfo.of(
                element.getAsJsonObject().get("sub").getAsString(),
                element.getAsJsonObject().get("email").getAsString()
        );
    }
}
