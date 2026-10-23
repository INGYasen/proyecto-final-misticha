package com.raymisupreme.msauth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "ms-auth API",
        version = "1.0",
        description = "Gestion de usuarios via Keycloak Admin API. Requiere rol SUPER_ADMIN."
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Ingresa el JWT obtenido del endpoint de Keycloak: " +
                  "POST /realms/raymisupreme-dev/protocol/openid-connect/token"
)
public class OpenApiConfig {
}
