package com.raymisupreme.mspagos.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class FeignSecurityConfig {
    @Bean
    RequestInterceptor jwtRequestInterceptor() {
        return template -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) return;
            Jwt jwt = authentication.getPrincipal() instanceof Jwt principal ? principal
                    : authentication.getCredentials() instanceof Jwt credentials ? credentials : null;
            if (jwt != null) {
                template.header("Authorization", "Bearer " + jwt.getTokenValue());
            }
        };
    }
}
