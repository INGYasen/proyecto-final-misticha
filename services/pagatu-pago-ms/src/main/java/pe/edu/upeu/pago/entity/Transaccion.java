package pe.edu.upeu.pago.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transacciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_pago", nullable = false)
    private Long pagoId;

    @Column(name = "referencia", nullable = false, length = 80)
    private String referencia;

    @Column(name = "canal", nullable = false, length = 40)
    private String canal;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}
