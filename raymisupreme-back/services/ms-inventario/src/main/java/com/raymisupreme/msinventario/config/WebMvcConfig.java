package com.raymisupreme.msinventario.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración para servir imágenes subidas como recursos estáticos.
 * Las imágenes guardadas en `uploads/imagenes/` quedarán accesibles en:
 * http://localhost:7091/images/prendas/{filename}
 *
 * INSTRUCCIONES:
 * 1. Copiar este archivo a: ms-inventario/src/main/java/.../config/WebMvcConfig.java
 * 2. Agregar en application.yml:
 *      app:
 *        upload:
 *          dir: uploads/imagenes
 *        base-url: http://localhost:7091
 * 3. Agregar en application.yml bajo spring.servlet.multipart:
 *      multipart:
 *        max-file-size: 5MB
 *        max-request-size: 10MB
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:../../images/prendas}")
    private String uploadDir;

    /**
     * Mapea /images/prendas/** → carpeta local uploads/imagenes/
     * Así el frontend puede acceder con:
     * <img src="http://localhost:7091/images/prendas/uuid.jpg">
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        String uploadAbsolutePath = uploadPath.toUri().toString();

        registry
            .addResourceHandler("/images/prendas/**")
            .addResourceLocations(uploadAbsolutePath)
            .setCachePeriod(3600);  // 1 hora de caché en cliente
    }


}
