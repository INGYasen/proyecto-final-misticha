-- Tabla: Categorías de prendas (Chompas, Chullos, etc.)
CREATE TABLE IF NOT EXISTS categoria (
                                         id_categoria BIGINT NOT NULL AUTO_INCREMENT,
                                         nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    PRIMARY KEY (id_categoria)
    );

-- Tabla Principal: Prenda (El producto físico)
CREATE TABLE IF NOT EXISTS prenda (
                                      id_prenda BIGINT NOT NULL AUTO_INCREMENT,

    -- OJO AQUÍ: id_lote es una referencia a ms-acopio. NO lleva "CONSTRAINT FOREIGN KEY"
                                      id_lote BIGINT NOT NULL,

                                      id_categoria BIGINT NOT NULL,
                                      codigo_sku VARCHAR(50) NOT NULL UNIQUE,
    talla VARCHAR(20),
    color VARCHAR(50),
    material VARCHAR(100),
    stock INT NOT NULL DEFAULT 0,

    PRIMARY KEY (id_prenda),
    -- Esta FK sí se puede porque 'categoria' está en esta misma base de datos
    CONSTRAINT fk_prenda_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
    );

-- Tabla: Control de Calidad
CREATE TABLE IF NOT EXISTS inspeccion_calidad (
                                                  id_inspeccion BIGINT NOT NULL AUTO_INCREMENT,
                                                  id_prenda BIGINT NOT NULL,
                                                  fecha_inspeccion DATE NOT NULL,
                                                  resultado VARCHAR(50) NOT NULL, -- Ej: "Apto para venta", "Requiere arreglo", "Descartado"
    observaciones VARCHAR(255),

    PRIMARY KEY (id_inspeccion),
    -- Esta FK sí se puede porque 'prenda' está en esta misma base de datos
    CONSTRAINT fk_inspeccion_prenda FOREIGN KEY (id_prenda) REFERENCES prenda(id_prenda)
    );
