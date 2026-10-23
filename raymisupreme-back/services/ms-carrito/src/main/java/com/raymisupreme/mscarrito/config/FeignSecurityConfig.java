package com.raymisupreme.mscarrito.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Interceptor Feign que propaga el JWT del usuario actual a todas las
 * llamadas internas salientes (ms-inventario, ms-pagos, ms-catalogo).
 *
 * Sin esto, los microservicios destino responden 401 porque reciben
 * el request sin cabecera Authorization.
 *
 * No se declara como @Bean global para no afectar otros contextos;
 * se referencia en cada @FeignClient mediante configuration = FeignSecurityConfig.class
 */
@Configuration
public class FeignSecurityConfig {

    @Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return template -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof AbstractAuthenticationToken token) {
                var credentials = token.getCredentials();
                if (credentials instanceof Jwt jwt) {
                    template.header("Authorization", "Bearer " + jwt.getTokenValue());
                }
            }
        };
    }
}
