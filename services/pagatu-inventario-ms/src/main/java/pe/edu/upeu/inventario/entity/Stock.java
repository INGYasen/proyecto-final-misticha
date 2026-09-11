package pe.edu.upeu.inventario.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_producto", nullable = false, unique = true)
    private Long idProducto;

    @Column(name = "sku", nullable = false, unique = true, length = 40)
    private String sku;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "minimo", nullable = false)
    private Integer minimo;

    @Column(name = "almacen", nullable = false, length = 80)
    private String almacen;
}
