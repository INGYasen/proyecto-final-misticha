package pe.edu.upeu.inventario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("chaskawear-inventario-ms API")
                .version("0.0.1-SNAPSHOT")
                .description("Stock y movimientos de prendas artesanales. El producto se referencia por idProducto porque vive en catalogo-ms."));
    }
}
