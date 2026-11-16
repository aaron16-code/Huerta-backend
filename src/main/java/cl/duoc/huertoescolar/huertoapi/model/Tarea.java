package cl.duoc.huertoescolar.huertoapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Una tarea del huerto. Pertenece a un sector y se relaciona con varios usuarios. Fechas en epoch millis. */
@Entity
@Table(name = "tareas")
@Getter
@Setter
@NoArgsConstructor
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoTarea tipo;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, length = 1000)
    private String instrucciones;

    // N tareas -> 1 sector (obligatorio)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    // N tareas -> 1 cultivo (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cultivo_id")
    private Cultivo cultivo;

    private Long fechaCreacion;

    @Column(nullable = false)
    private Long fechaProgramada;

    // Estudiante asignado... (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;

    // ...o un grupo completo
    private String grupoAsignado;

    // Quien creó la tarea (obligatorio)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creada_por_id", nullable = false)
    private Usuario creadaPor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoTarea estado = EstadoTarea.PENDIENTE;

    private Long fechaRealizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realizada_por_id")
    private Usuario realizadaPor;

    @Column(length = 1000)
    private String fotoUri;

    @Column(length = 1000)
    private String comentarioEstudiante = "";

    @Column(length = 1000)
    private String comentarioDocente = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validador_id")
    private Usuario validador;

    private Long fechaValidacion;
}
