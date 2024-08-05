package com.kkokkomu.short_news.security.config;

import com.kkokkomu.short_news.constant.Constant;
import com.kkokkomu.short_news.security.filter.CustomLogoutFilter;
import com.kkokkomu.short_news.security.filter.JwtAuthenticationFilter;
import com.kkokkomu.short_news.security.filter.JwtExceptionFilter;
import com.kkokkomu.short_news.security.handler.CustomSignOutProcessHandler;
import com.kkokkomu.short_news.security.handler.CustomSignOutResultHandler;
import com.kkokkomu.short_news.security.provider.JwtAuthenticationProvider;
import com.kkokkomu.short_news.security.service.CustomUserDetailsService;
import com.kkokkomu.short_news.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomSignOutProcessHandler customSignOutProcessHandler;
    private final CustomSignOutResultHandler customSignOutResultHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(Constant.NO_NEED_AUTH_URLS.toArray(new String[0])).permitAll()
                                .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(configurer ->
                        configurer
                                .logoutUrl("/oauth2/logout")
                                .addLogoutHandler(customSignOutProcessHandler)
                                .logoutSuccessHandler(customSignOutResultHandler)
                                .deleteCookies(Constant.AUTHORIZATION_HEADER, Constant.REAUTHORIZATION))
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, new JwtAuthenticationProvider(customUserDetailsService, bCryptPasswordEncoder)), LogoutFilter.class)
                .addFilterAfter(new JwtExceptionFilter(), JwtAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(), LogoutFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
                        })
                )
                .build();
    }
}