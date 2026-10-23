package com.raymisupreme.mspagos.config;
// NOTA: Cambia el package según el microservicio:

//   ms-acopio     → com.raymisupreme.msacopio.config
//   ms-inventario → com.raymisupreme.msinventario.config
//   ms-ventas     → com.raymisupreme.msventas.config
// El contenido de la clase es IDÉNTICO en los tres.

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuración de seguridad para microservicios (Servlet / WebMVC).
 *
 * Los microservicios confían en el JWT que el Gateway ya validó y reenviará.
 * Aquí se valida nuevamente el JWT (defensa en profundidad) y se extraen roles
 * para poder usar @PreAuthorize en los controllers.
 *
 * Roles internos:
 * SUPER_ADMIN -> acceso total.
 * VENDEDORA -> sin acceso a informacion financiera sensible.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // habilita @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/pagos/mercadopago/webhook", "/api/pagos/mercadopago/webhook")
                        .permitAll()
                        // Actuator health: público (para Docker healthchecks)
                        .requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus",
                                "/actuator/metrics")
                        .permitAll()
                        // Swagger: accesible (el Gateway ya puede proteger esto si quieres)
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**")
                        .permitAll()
                        // Todo lo demás requiere token válido
                        // (los roles específicos se controlan con @PreAuthorize en cada endpoint)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRolesConverter());
        return converter;
    }

    /**
     * Convierte roles de Keycloak al formato ROLE_SUPER_ADMIN / ROLE_VENDEDORA.
     * Lee primero el claim "roles" y luego realm_access.roles.
     */
    static class KeycloakRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            List<String> roles = jwt.getClaimAsStringList("roles");

            if (roles == null || roles.isEmpty()) {
                Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
                if (realmAccess != null && realmAccess.containsKey("roles")) {
                    // noinspection unchecked
                    roles = (List<String>) realmAccess.get("roles");
                }
            }

            if (roles == null)
                return Collections.emptyList();

            return roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }
}
