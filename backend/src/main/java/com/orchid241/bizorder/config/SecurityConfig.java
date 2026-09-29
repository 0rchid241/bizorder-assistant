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
        // Issue #1: 인증 구현 전까지 health 조회만 공개한다. 실제 권한 정책은 후속 Issue에서 구성한다.
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                .anyRequest().denyAll());
        return http.build();
    }
}
