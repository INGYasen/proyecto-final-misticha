package pe.edu.upeu.notificacion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "avisos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aviso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_canal", nullable = false)
    private Long canalId;

    @Column(name = "destinatario", nullable = false, length = 120)
    private String destinatario;

    @Column(name = "asunto", nullable = false, length = 120)
    private String asunto;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}
