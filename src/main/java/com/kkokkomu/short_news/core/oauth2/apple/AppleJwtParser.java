package com.kkokkomu.short_news.core.oauth2.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

@Component
public class AppleJwtParser {
    private static final String TOKEN_VALUE_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Apple IdToken header에 ALG와 KID 추출
    public Map<String, String> parseHeaders(String idToken) {
        try {
            String encodedHeader = idToken.split(TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            return OBJECT_MAPPER.readValue(decodedHeader, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            throw new CommonException(ErrorCode.INVALID_APPLE_IDENTITY_TOKEN_ERROR);
        }
    }

    // PublicKey로 Identity Token의 Claim을 추출
    public Claims parseClaims(String idToken, PublicKey publicKey) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new CommonException(ErrorCode.EXPIRED_APPLE_IDENTITY_TOKEN_ERROR);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new CommonException(ErrorCode.INVALID_APPLE_IDENTITY_TOKEN_ERROR);
        }
    }
}