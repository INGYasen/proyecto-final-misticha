package com.raymisupreme.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    static final String ISSUER = "http://localhost:9091/realms/raymisupreme-dev";
    static final Set<String> AUTHORIZED_PARTIES = Set.of("raymisupreme-app", "raymisupreme-spa");

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withIssuerLocation(ISSUER).build();
        OAuth2TokenValidator<Jwt> party = token -> {
            if (!AUTHORIZED_PARTIES.contains(token.getClaimAsString("azp"))) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error(
                        "invalid_token", "El token no pertenece a la tienda", null));
            }
            if (token.getClaimAsString("typ") != null && !"Bearer".equals(token.getClaimAsString("typ"))) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error(
                        "invalid_token", "Tipo de token no aceptado", null));
            }
            return OAuth2TokenValidatorResult.success();
        };
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(ISSUER), party));
        return decoder;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // 1. Habilitar CORS aquí:
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:4200")); // Tu frontend
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus",
                                "/actuator/metrics", "/fallback/**")
                        .permitAll()
                        .pathMatchers(
                                "/ms-acopio/swagger-ui/**", "/ms-acopio/v3/api-docs/**",
                                "/ms-inventario/swagger-ui/**", "/ms-inventario/v3/api-docs/**",
                                "/ms-ventas/swagger-ui/**", "/ms-ventas/v3/api-docs/**",
                                "/ms-auth/swagger-ui/**", "/ms-auth/v3/api-docs/**",
                                "/ms-catalogo/swagger-ui/**", "/ms-catalogo/v3/api-docs/**",
                                "/ms-pagos/swagger-ui/**", "/ms-pagos/v3/api-docs/**",
                                "/ms-carrito/swagger-ui/**", "/ms-carrito/v3/api-docs/**")
                        .permitAll()

                        // Tienda web publica.
                        .pathMatchers("/api/catalogo/public/**","/api/v1/prendas/public/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/catalogo/productos",
                                "/api/v1/catalogo/productos/**","/api/v1/precios/**").permitAll()
                        .pathMatchers("/api/promociones/public/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/images/prendas/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth").permitAll()
                        .pathMatchers("/api/v1/auth", "/api/v1/auth/**").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST,
                                "/api/v1/pagos/mercadopago/webhook",
                                "/api/pagos/mercadopago/webhook").permitAll()

                        // Compra web: requiere token valido, sin rol CLIENTE por ahora.
                        .pathMatchers(HttpMethod.GET, "/api/v1/clientes/me").authenticated()
                        .pathMatchers(HttpMethod.GET, "/api/v1/pedidos/*", "/api/v1/detalles").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/v1/clientes/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/v1/ventas/checkout/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/v1/pedidos/*/cancelar").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/v1/pedidos/*/confirmar").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/pedidos/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/v1/detalles/**", "/api/v1/detalles-pedido/**")
                        .authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/pagos/procesar", "/api/v1/pagos/procesar").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST,
                                "/api/v1/pagos/mercadopago/payment",
                                "/api/v1/pagos/mercadopago/preferencia",
                                "/api/pagos/mercadopago/payment",
                                "/api/pagos/mercadopago/preferencia").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/v1/pagos/stripe/checkout-session").authenticated()
                        .pathMatchers(HttpMethod.GET, "/api/v1/pagos/stripe/session/**").authenticated()
                        .pathMatchers("/api/carrito/**", "/api/v1/carrito/**").authenticated()

                        // Info interna, configuracion critica y usuarios administrativos.
                        .pathMatchers(
                                "/api/v1/acopio/instancia",
                                "/api/v1/inventario/instancia",
                                "/api/v1/ventas/instancia",
                                "/api/v1/catalogo/instancia",
                                "/api/v1/precios/instancia")
                        .hasRole("SUPER_ADMIN")

                        // Acopio: consulta operativa para ventas, cambios solo SUPER_ADMIN.
                        .pathMatchers(HttpMethod.GET, "/api/v1/artesanos/**", "/api/v1/lotes/**")
                        .hasAnyRole("SUPER_ADMIN", "VENDEDORA")
                        .pathMatchers("/api/v1/artesanos/**", "/api/v1/lotes/**")
                        .hasRole("SUPER_ADMIN")

                        // Inventario: lectura para ventas, cambios solo SUPER_ADMIN.
                        .pathMatchers(HttpMethod.GET, "/api/v1/categorias/**", "/api/v1/prendas/**",
                                "/api/v1/inspecciones/**","/api/v1/imagenes/**")
                        .hasAnyRole("SUPER_ADMIN", "VENDEDORA")
                        //.permitAll()
                        .pathMatchers("/api/v1/categorias/**", "/api/v1/prendas/**", "/api/v1/inspecciones/**")
                        .hasRole("SUPER_ADMIN")

                        // Ventas: lectura y atencion de pedidos para VENDEDORA.
                        .pathMatchers(HttpMethod.GET, "/api/v1/clientes/**", "/api/v1/pedidos/**",
                                "/api/v1/detalles/**", "/api/v1/detalles-pedido/**")
                        .hasAnyRole("SUPER_ADMIN", "VENDEDORA")
                        .pathMatchers("/api/v1/clientes/**", "/api/v1/pedidos/**", "/api/v1/detalles/**",
                                "/api/v1/detalles-pedido/**")
                        .hasRole("SUPER_ADMIN")

                        // Catalogo administrable y precios.
                        .pathMatchers(HttpMethod.GET, "/api/v1/catalogo/items/**", "/api/v1/precios/**",
                                "/api/precios/**")
                        .hasAnyRole("SUPER_ADMIN", "VENDEDORA")
                        .pathMatchers("/api/v1/catalogo/items/**", "/api/v1/precios/**", "/api/precios/**")
                        .hasRole("SUPER_ADMIN")

                        // Pagos y reportes financieros: VENDEDORA no accede a informacion sensible.
                        .pathMatchers("/api/pagos/**", "/api/v1/pagos/**").hasRole("SUPER_ADMIN")

                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRolesConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
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

            if (roles == null) {
                return Collections.emptyList();
            }

            return roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }
}
