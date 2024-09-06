package com.kkokkomu.short_news.core.oauth2.apple;

public record ApplePublicKey(
        String kty,
        String kid,
        String use,
        String alg,
        String n,
        String e
) {
}
