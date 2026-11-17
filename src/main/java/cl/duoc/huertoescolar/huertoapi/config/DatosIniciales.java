package cl.duoc.huertoescolar.huertoapi.config;

import cl.duoc.huertoescolar.huertoapi.model.Cultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoCultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoReporte;
import cl.duoc.huertoescolar.huertoapi.model.EstadoSector;
import cl.duoc.huertoescolar.huertoapi.model.EstadoTarea;
import cl.duoc.huertoescolar.huertoapi.model.Problema;
import cl.duoc.huertoescolar.huertoapi.model.TipoProblema;
import cl.duoc.huertoescolar.huertoapi.model.TipoTarea;
import cl.duoc.huertoescolar.huertoapi.model.Tarea;
import cl.duoc.huertoescolar.huertoapi.model.Rol;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.model.Usuario;
import cl.duoc.huertoescolar.huertoapi.repository.CultivoRepository;
import cl.duoc.huertoescolar.huertoapi.repository.ProblemaRepository;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
import cl.duoc.huertoescolar.huertoapi.repository.TareaRepository;
import cl.duoc.huertoescolar.huertoapi.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Datos de ejemplo la primera vez que arranca el servidor con la base vacía.
 * Los usuarios quedan con id 1 a 4, igual que las cuentas de prueba de la app.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final SectorRepository sectores;
    private final CultivoRepository cultivos;
    private final TareaRepository tareas;
    private final ProblemaRepository problemas;

    private static final long HORA = 60L * 60 * 1000;
    private static final long DIA = 24 * HORA;

    public DatosIniciales(UsuarioRepository usuarios, SectorRepository sectores, CultivoRepository cultivos,
                          TareaRepository tareas, ProblemaRepository problemas) {
        this.usuarios = usuarios;
        this.sectores = sectores;
        this.cultivos = cultivos;
        this.tareas = tareas;
        this.problemas = problemas;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() == 0) {
            usuarios.save(usuario("Profesora Laura", Rol.ADMINISTRADOR, ""));
            usuarios.save(usuario("Sofía Martínez", Rol.ESTUDIANTE, "Taller Huerto A"));
            usuarios.save(usuario("Tomás González", Rol.ESTUDIANTE, "Taller Huerto A"));
            usuarios.save(usuario("Valentina Rojas", Rol.ESTUDIANTE, "Taller Huerto B"));
        }
        if (sectores.count() == 0) {
            Usuario responsable = usuarios.findAll().stream().findFirst().orElse(null);
            // Mismos ids que usan los datos de ejemplo de la app: sectores 1 a 3, cultivos 1 a 4
            Sector norte = guardarSector("Bancal 1", "Sector de hortalizas", "Zona norte", EstadoSector.EN_USO, responsable);
            Sector sur = guardarSector("Bancal 2", "Sector de tomates y zanahorias", "Zona sur", EstadoSector.EN_USO, responsable);
            Sector invernadero = guardarSector("Invernadero", "Sector protegido para hierbas", "Patio central", EstadoSector.EN_USO, responsable);

            guardarCultivo("Lechuga", "Lechuga de hoja", norte, "01/10/2026", "30/11/2026", 2, EstadoCultivo.EN_CRECIMIENTO);
            guardarCultivo("Tomate", "Tomate cherry", sur, "15/09/2026", "15/12/2026", 2, EstadoCultivo.EN_CRECIMIENTO);
            guardarCultivo("Zanahoria", "Zanahoria en surcos", norte, "05/10/2026", "10/01/2027", 3, EstadoCultivo.SEMBRADO);
            guardarCultivo("Albahaca", "Albahaca en macetas", invernadero, "20/10/2026", "20/12/2026", 1, EstadoCultivo.SEMBRADO);
        }
        if (tareas.count() == 0) {
            cargarTareas();
        }
        if (problemas.count() == 0) {
            cargarProblemas();
        }
    }

    private static long enDias(int n) {
        return System.currentTimeMillis() + n * DIA;
    }

    private static long haceHoras(int h) {
        return System.currentTimeMillis() - h * HORA;
    }

    /** Mismas tareas de ejemplo que trae la app, para que la lista no parta vacía. */
    private void cargarTareas() {
        tareas.save(tarea(TipoTarea.RIEGO, "Regar la lechuga",
                "Riega con regadera hasta humedecer la tierra, sin encharcar.",
                1, 1L, enDias(-6), enDias(-2), 2, null, EstadoTarea.PENDIENTE));
        tareas.save(tarea(TipoTarea.RETIRO_MALEZAS, "Retirar malezas del sector norte",
                "Saca las malezas a mano y ponlas en el balde.",
                1, null, enDias(-1), enDias(1), 2, null, EstadoTarea.PENDIENTE));

        Tarea observar = tarea(TipoTarea.OBSERVACION, "Observar el tomate",
                "Revisa hojas y tallo y anota cómo se ve la planta.",
                2, 2L, enDias(-4), enDias(-1), 3, null, EstadoTarea.PENDIENTE_VALIDACION);
        observar.setFechaRealizacion(haceHoras(5));
        observar.setRealizadaPor(usuarioDe(3));
        observar.setFotoUri("foto_prueba_tomate");
        observar.setComentarioEstudiante("Las hojas se ven verdes y firmes.");
        tareas.save(observar);

        Tarea sembrar = tarea(TipoTarea.SIEMBRA, "Sembrar zanahoria",
                "Haz surcos de 2 cm y deja caer las semillas separadas.",
                1, 3L, enDias(-9), enDias(-5), 2, null, EstadoTarea.VALIDADA);
        sembrar.setFechaRealizacion(enDias(-5));
        sembrar.setRealizadaPor(usuarioDe(2));
        sembrar.setComentarioEstudiante("Listo, sembrado.");
        sembrar.setComentarioDocente("Muy bien, buen espaciado.");
        sembrar.setValidador(usuarioDe(1));
        sembrar.setFechaValidacion(enDias(-4));
        tareas.save(sembrar);

        tareas.save(tarea(TipoTarea.RIEGO, "Regar la albahaca del invernadero", "Un vaso de agua por maceta.",
                3, 4L, enDias(-1), enDias(1), null, "Taller Huerto A", EstadoTarea.PENDIENTE));

        Tarea limpiar = tarea(TipoTarea.LIMPIEZA, "Limpiar el sector sur", "Recoge hojas secas y restos.",
                2, null, enDias(-7), enDias(-3), 2, null, EstadoTarea.RECHAZADA);
        limpiar.setFechaRealizacion(enDias(-3));
        limpiar.setRealizadaPor(usuarioDe(2));
        limpiar.setComentarioEstudiante("Terminé.");
        limpiar.setComentarioDocente("Faltó retirar los restos del fondo. Repítela.");
        limpiar.setValidador(usuarioDe(1));
        limpiar.setFechaValidacion(enDias(-2));
        tareas.save(limpiar);

        tareas.save(tarea(TipoTarea.COSECHA, "Cosechar la lechuga", "Corta las hojas exteriores con cuidado.",
                1, 1L, enDias(-1), enDias(6), 4, null, EstadoTarea.PENDIENTE));
    }

    private void cargarProblemas() {
        Problema seco = problema(1, 1L, TipoProblema.FALTA_AGUA, "La tierra de la lechuga está muy seca.",
                haceHoras(8), 2, EstadoReporte.INFORMADO);
        seco.setFotoUri("foto_prueba_lechuga");
        problemas.save(seco);

        Problema manchas = problema(2, 2L, TipoProblema.PLAGA_SIMULADA,
                "Se ven manchas en las hojas del tomate (prueba).", haceHoras(30), 3, EstadoReporte.EN_REVISION);
        manchas.setComentarioDocente("Lo estamos revisando.");
        problemas.save(manchas);

        Problema tapa = problema(3, null, TipoProblema.DETERIORO_SECTOR, "La tapa del invernadero estaba floja.",
                haceHoras(80), 2, EstadoReporte.CERRADO);
        tapa.setComentarioDocente("Se ajustó la tapa.");
        tapa.setFechaCierre(haceHoras(50));
        problemas.save(tapa);
    }

    private Tarea tarea(TipoTarea tipo, String descripcion, String instrucciones, int sectorId, Long cultivoId,
                        long creada, long programada, Integer responsableId, String grupo, EstadoTarea estado) {
        Tarea t = new Tarea();
        t.setTipo(tipo);
        t.setDescripcion(descripcion);
        t.setInstrucciones(instrucciones);
        t.setSector(sectorDe(sectorId));
        t.setCultivo(cultivoId == null ? null : cultivoDe(cultivoId.intValue()));
        t.setFechaCreacion(creada);
        t.setFechaProgramada(programada);
        t.setResponsable(responsableId == null ? null : usuarioDe(responsableId));
        t.setGrupoAsignado(grupo);
        t.setCreadaPor(usuarioDe(1));
        t.setEstado(estado);
        return t;
    }

    private Problema problema(int sectorId, Long cultivoId, TipoProblema tipo, String descripcion,
                              long fecha, int reportadoPorId, EstadoReporte estado) {
        Problema p = new Problema();
        p.setSector(sectorDe(sectorId));
        p.setCultivo(cultivoId == null ? null : cultivoDe(cultivoId.intValue()));
        p.setTipo(tipo);
        p.setDescripcion(descripcion);
        p.setFechaReporte(fecha);
        p.setReportadoPor(usuarioDe(reportadoPorId));
        p.setEstado(estado);
        return p;
    }

    private Sector sectorDe(int id) {
        return sectores.findById(id).orElseThrow();
    }

    private Cultivo cultivoDe(int id) {
        return cultivos.findById(id).orElseThrow();
    }

    private Usuario usuarioDe(int id) {
        return usuarios.findById(id).orElseThrow();
    }

    private Sector guardarSector(String nombre, String descripcion, String ubicacion, EstadoSector estado, Usuario responsable) {
        Sector sector = new Sector();
        sector.setNombre(nombre);
        sector.setDescripcion(descripcion);
        sector.setUbicacion(ubicacion);
        sector.setEstado(estado);
        sector.setResponsable(responsable);
        return sectores.save(sector);
    }

    private void guardarCultivo(String nombre, String descripcion, Sector sector, String siembra,
                                String cosecha, int riegoCadaDias, EstadoCultivo estado) {
        Cultivo cultivo = new Cultivo();
        cultivo.setNombre(nombre);
        cultivo.setDescripcion(descripcion);
        cultivo.setSector(sector);
        cultivo.setFechaSiembra(siembra);
        cultivo.setFechaCosechaEstimada(cosecha);
        cultivo.setFrecuenciaRiegoDias(riegoCadaDias);
        cultivo.setEstado(estado);
        cultivo.setObservaciones("");
        cultivos.save(cultivo);
    }

    private Usuario usuario(String nombre, Rol rol, String grupo) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setRol(rol);
        u.setGrupo(grupo);
        return u;
    }
}