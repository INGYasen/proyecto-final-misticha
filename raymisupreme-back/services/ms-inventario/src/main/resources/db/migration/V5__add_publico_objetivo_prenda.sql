ALTER TABLE prenda
    ADD COLUMN publico VARCHAR(20) NOT NULL DEFAULT 'UNISEX' AFTER id_categoria;

CREATE INDEX idx_prenda_publico ON prenda (publico);
CREATE INDEX idx_prenda_categoria_publico ON prenda (id_categoria, publico);
