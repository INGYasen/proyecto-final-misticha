package pe.edu.upeu.orden.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ordenOpenApi() {
        return new OpenAPI().info(new Info()
                .title("pagatu-orden-ms API")
                .version("0.0.1-SNAPSHOT")
                .description("Microservicio de ordenes del sistema distribuido pagatu. "
                        + "Gestiona ordenes de compra y sus detalles; los productos se referencian "
                        + "por id porque pertenecen a pagatu-catalogo-ms."));
    }
}
