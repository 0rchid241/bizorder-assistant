package com.orchid241.bizorder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 임시 개발 정책: Issue #12에서 STAFF / ADMIN 권한 정책으로 교체한다.
        // 인증 쿠키를 사용하지 않는 Product API에 한해서만 CSRF 검사를 제외한다.
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/products", "/api/products/*"));
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/products/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/v3/api-docs", "/v3/api-docs/**",
                        "/swagger-ui.html", "/swagger-ui/**").permitAll()
                .anyRequest().denyAll());
        return http.build();
    }
}
