-- Creación de la tabla para los Artesanos de Puno/Juliaca
CREATE TABLE IF NOT EXISTS artesano (
                                        id_artesano BIGINT NOT NULL AUTO_INCREMENT,
                                        nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(15) NOT NULL,
    comunidad_o_zona VARCHAR(100),
    telefono VARCHAR(20),
    PRIMARY KEY (id_artesano)
    );

-- Creación de la tabla para el Saco/Lote que te entregan
CREATE TABLE IF NOT EXISTS lote_compra (
                                           id_lote BIGINT NOT NULL AUTO_INCREMENT,
                                           id_artesano BIGINT NOT NULL,
                                           fecha_compra DATE NOT NULL,
                                           precio_total_pagado DECIMAL(10, 2) NOT NULL,
    cantidad_total_recibida INT NOT NULL,
    descripcion_general VARCHAR(255),
    estado_lote VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_lote),
    CONSTRAINT fk_lote_artesano FOREIGN KEY (id_artesano) REFERENCES artesano(id_artesano)
    );