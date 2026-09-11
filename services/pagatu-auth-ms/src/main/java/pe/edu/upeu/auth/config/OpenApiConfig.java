package pe.edu.upeu.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("chaskawear-auth-ms API")
                .version("0.0.1-SNAPSHOT")
                .description("Usuarios y roles. En Unidad 2 se conecta JWT al Gateway."));
    }
}
