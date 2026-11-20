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

/** Un problema reportado por un estudiante en un sector (y opcionalmente en un cultivo). */
@Entity
@Table(name = "problemas")
@Getter
@Setter
@NoArgsConstructor
public class Problema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private Sector sector;

    public Sector sectorEfectivo() { return cultivo == null ? sector : cultivo.getSector(); }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cultivo_id")
    private Cultivo cultivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoProblema tipo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    private Long fechaReporte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reportado_por_id", nullable = false)
    private Usuario reportadoPor;

    @Column(length = 1000)
    private String fotoUri;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoReporte estado = EstadoReporte.INFORMADO;

    @Column(length = 1000)
    private String comentarioDocente = "";

    private Long fechaCierre;
}
