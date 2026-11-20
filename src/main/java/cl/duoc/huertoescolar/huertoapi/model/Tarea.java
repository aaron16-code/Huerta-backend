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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private Sector sector;

    public Sector sectorEfectivo() { return cultivo == null ? sector : cultivo.getSector(); }

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
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="grupo_asignado_id")
    private Grupo grupoAsignado;

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




    @jakarta.persistence.OneToMany(mappedBy="tarea", cascade=jakarta.persistence.CascadeType.ALL)
    @jakarta.persistence.OrderBy("id ASC")
    private java.util.List<Evidencia> evidencias = new java.util.ArrayList<>();
    @jakarta.persistence.OneToMany(mappedBy="tarea", cascade=jakarta.persistence.CascadeType.ALL)
    @jakarta.persistence.OrderBy("id ASC")
    private java.util.List<Validacion> validaciones = new java.util.ArrayList<>();
    public Evidencia ultimaEvidencia() { return evidencias.isEmpty()?null:evidencias.get(evidencias.size()-1); }
    public Validacion ultimaValidacion() { return validaciones.isEmpty()?null:validaciones.get(validaciones.size()-1); }
}
