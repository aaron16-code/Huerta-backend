package cl.duoc.huertoescolar.huertoapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Un usuario (docente o estudiante). Un usuario tiene muchas tareas y puede reportar muchos problemas. */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(nullable = false)
    private String grupo = "";

    // 1 usuario -> N tareas que tiene asignadas
    @OneToMany(mappedBy = "responsable")
    private List<Tarea> tareasAsignadas = new ArrayList<>();

    // 1 usuario -> N tareas que creó
    @OneToMany(mappedBy = "creadaPor")
    private List<Tarea> tareasCreadas = new ArrayList<>();

    // 1 usuario -> N problemas que reportó
    @OneToMany(mappedBy = "reportadoPor")
    private List<Problema> problemasReportados = new ArrayList<>();
}
