package com.kkokkomu.short_news.core.oauth2;

public record OAuth2UserInfo (
        String oAuthId,    // 구글/애플 sub, 카카오 id
        String email
) {
    public static OAuth2UserInfo of(String oAuthId, String email) {
        return new OAuth2UserInfo(oAuthId, email);
    }
}
