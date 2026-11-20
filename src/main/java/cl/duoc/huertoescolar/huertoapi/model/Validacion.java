package cl.duoc.huertoescolar.huertoapi.model;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="validaciones") @Getter @Setter @NoArgsConstructor
public class Validacion {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
@ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="tarea_id",nullable=false) private Tarea tarea;
@ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="validador_id",nullable=false) private Usuario validador;
@Enumerated(EnumType.STRING) @Column(nullable=false) private ResultadoValidacion resultado;
@Column(length=1000) private String comentario;
@Column(nullable=false) private Long fechaValidacion;
}
