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

/** Un sector del huerto. Un sector tiene muchos cultivos, tareas y problemas. */
@Entity
@Table(name = "sectores")
@Getter
@Setter
@NoArgsConstructor
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(length = 500)
    private String descripcion = "";

    private String ubicacion = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSector estado = EstadoSector.DISPONIBLE;

    // N sectores -> 1 usuario responsable (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;

    @OneToMany(mappedBy = "sector")
    private List<Cultivo> cultivos = new ArrayList<>();

    @OneToMany(mappedBy = "sector")
    private List<Tarea> tareas = new ArrayList<>();

    @OneToMany(mappedBy = "sector")
    private List<Problema> problemas = new ArrayList<>();
}
