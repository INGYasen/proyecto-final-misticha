CREATE TABLE item_catalogo (
    id_item BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_prenda BIGINT NOT NULL,
    url_foto VARCHAR(500) NOT NULL,
    descripcion_publica VARCHAR(500),
    destacado BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_item_catalogo_prenda UNIQUE (id_prenda)
);
