
package cl.duoc.huertoescolar.huertoapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "evidencias")
@Getter
@Setter
@NoArgsConstructor
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarea_id", nullable = false)
    private Tarea tarea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(length = 1000)
    private String fotoUri;

    @Column(length = 1000)
    private String comentario;

    private Long fechaRegistro;
}
