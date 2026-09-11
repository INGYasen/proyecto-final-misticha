package pe.edu.upeu.notificacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("chaskawear-notificacion-ms API")
                .version("0.0.1-SNAPSHOT")
                .description("Avisos al cliente por canal. En Unidad 2 se dispara por eventos Kafka."));
    }
}
