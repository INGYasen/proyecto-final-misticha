package com.raymisupreme.msventas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ventasOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-ventas API")
                        .description("API REST del microservicio de ventas: clientes, pedidos y detalles de pedido.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo RaymiSupreme")
                                .email("dev@raymisupreme.pe"))
                        .license(new License()
                                .name("Internal Use Only")));
    }
}
