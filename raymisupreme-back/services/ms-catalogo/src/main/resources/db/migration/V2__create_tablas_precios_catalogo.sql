CREATE TABLE IF NOT EXISTS descuento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    porcentaje DECIMAL(5,2) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS precio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prenda_id BIGINT NOT NULL,
    nombre VARCHAR(255),
    precio_base DECIMAL(10,2) NOT NULL,
    precio_final DECIMAL(10,2) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    descuento_id BIGINT,
    CONSTRAINT fk_precio_descuento FOREIGN KEY (descuento_id) REFERENCES descuento (id)
);
