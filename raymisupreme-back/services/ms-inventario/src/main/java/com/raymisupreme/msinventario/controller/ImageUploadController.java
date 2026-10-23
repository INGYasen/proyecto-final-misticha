package com.raymisupreme.msinventario.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador para subida de imágenes de prendas.
 * Las imágenes se guardan en disco local y se sirven como recursos estáticos.
 * Para producción, reemplazar con un servicio de almacenamiento en la nube (S3, GCS, etc.)
 */
@RestController
@RequestMapping("/api/v1/imagenes")
public class ImageUploadController {

    @Value("${app.upload.dir:../../images/prendas}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:7091}")
    private String baseUrl;

    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private DataSize maxFileSize;

    /**
     * POST /api/v1/imagenes/prendas
     * Recibe un archivo de imagen, lo guarda con un nombre único
     * y retorna la URL pública para usar en la prenda.
     */
    @PostMapping("/prendas")
    public ResponseEntity<Map<String, String>> uploadImagenPrenda(
            @RequestParam("imagen") MultipartFile file) {
        System.out.println("Content-Type: " + file.getContentType());
        System.out.println("Nombre: " + file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El archivo está vacío"));
        }
        System.out.println("Content-Type: " + file.getContentType());
        System.out.println("Nombre: " + file.getOriginalFilename());
        // Validar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/jpeg") &&
                 !contentType.startsWith("image/png") &&
                 !contentType.startsWith("image/webp") &&
                 !contentType.startsWith("image/gif"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Solo se permiten imágenes JPG, PNG, WEBP o GIF"));
        }

        // Mantener esta validacion sincronizada con el limite multipart configurado.
        if (file.getSize() > maxFileSize.toBytes()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El archivo supera el limite de " + maxFileSize.toMegabytes() + "MB"));
        }

        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único
            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Guardar archivo
            Path filePath = uploadPath.resolve(uniqueFilename);
            System.out.println("Guardando imagen en: " + filePath.toAbsolutePath());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // URL pública de la imagen
            String imageUrl = baseUrl + "/images/prendas/" + uniqueFilename;

            return ResponseEntity.ok(Map.of(
                    "url", imageUrl,
                    "filename", uniqueFilename,
                    "mensaje", "Imagen subida correctamente"
            ));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al guardar la imagen: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/v1/imagenes/prendas/{filename}
     * Elimina la imagen del disco (llamar al editar/eliminar una prenda)
     */
    @DeleteMapping("/prendas/{filename}")
    public ResponseEntity<Map<String, String>> deleteImagen(
            @PathVariable String filename) {
        try {
            // Validar que no haya path traversal
            if (filename.contains("..") || filename.contains("/")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Nombre de archivo inválido"));
            }

            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return ResponseEntity.ok(Map.of("mensaje", "Imagen eliminada correctamente"));
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar: " + e.getMessage()));
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf('.'));
    }
}
