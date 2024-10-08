package com.kkokkomu.short_news.core.security.filter;

import com.kkokkomu.short_news.core.constant.Constant;
import com.kkokkomu.short_news.core.security.JwtAuthenticationToken;
import com.kkokkomu.short_news.core.security.info.JwtUserInfo;
import com.kkokkomu.short_news.core.type.EUserRole;
import com.kkokkomu.short_news.core.util.JwtUtil;
import com.kkokkomu.short_news.core.security.provider.JwtAuthenticationProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Constant.NO_NEED_AUTH_URLS.stream().anyMatch(request.getRequestURI()::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = jwtUtil.getJwtFromRequest(request);
        if (StringUtils.hasText(token)) {
            Claims claims = jwtUtil.validateAndGetClaimsFromToken(token);
            JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
                    .id(claims.get(Constant.USER_ID_CLAIM_NAME, Long.class))
                    .role(EUserRole.valueOf(claims.get(Constant.USER_ROLE_CLAIM_NAME, String.class)))
                    .build();

            JwtAuthenticationToken beforeAuthentication = new JwtAuthenticationToken(null, jwtUserInfo.id(), jwtUserInfo.role());

            UsernamePasswordAuthenticationToken afterAuthentication = (UsernamePasswordAuthenticationToken) jwtAuthenticationProvider.authenticate(beforeAuthentication);
            afterAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(afterAuthentication);
            SecurityContextHolder.setContext(securityContext);
        }

        filterChain.doFilter(request, response);
    }
}
