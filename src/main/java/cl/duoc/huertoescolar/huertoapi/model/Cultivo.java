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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Un cultivo plantado en un sector. Un cultivo puede tener muchas tareas y problemas. */
@Entity
@Table(name = "cultivos")
@Getter
@Setter
@NoArgsConstructor
public class Cultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion = "";

    // N cultivos -> 1 sector (obligatorio)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    // Fechas SQL DATE; el servicio conserva el formato del DTO Android.
    private java.time.LocalDate fechaSiembra;

    private java.time.LocalDate fechaCosechaEstimada;

    @Column(nullable = false)
    private Integer frecuenciaRiegoDias = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoCultivo estado = EstadoCultivo.PLANIFICADO;

    @Column(length = 1000)
    private String observaciones = "";

    @Column(length = 1000)
    private String fotoUri;

    @OneToMany(mappedBy = "cultivo")
    private List<Tarea> tareas = new ArrayList<>();

    @OneToMany(mappedBy = "cultivo")
    private List<Problema> problemas = new ArrayList<>();
}
