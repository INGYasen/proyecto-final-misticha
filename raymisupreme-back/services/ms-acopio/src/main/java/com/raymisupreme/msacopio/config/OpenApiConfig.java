package com.raymisupreme.msacopio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI acopioOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-acopio API")
                        .description("API REST del microservicio de acopio: artesanos y lotes de compra.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo RaymiSupreme")
                                .email("dev@raymisupreme.pe"))
                        .license(new License()
                                .name("Internal Use Only")));
    }
}
