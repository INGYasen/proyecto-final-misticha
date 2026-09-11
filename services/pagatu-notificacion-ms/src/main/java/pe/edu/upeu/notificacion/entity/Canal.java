package pe.edu.upeu.notificacion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "canales")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Canal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private Boolean activo;
}
