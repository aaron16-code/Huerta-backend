package cl.duoc.huertoescolar.huertoapi.service;

import static cl.duoc.huertoescolar.huertoapi.exception.Errores.invalido;
import static cl.duoc.huertoescolar.huertoapi.exception.Errores.noEncontrado;

import cl.duoc.huertoescolar.huertoapi.dto.TareaDto;
import cl.duoc.huertoescolar.huertoapi.model.Cultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoTarea;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.model.Tarea;
import cl.duoc.huertoescolar.huertoapi.model.Usuario;
import cl.duoc.huertoescolar.huertoapi.repository.CultivoRepository;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
import cl.duoc.huertoescolar.huertoapi.repository.TareaRepository;
import cl.duoc.huertoescolar.huertoapi.repository.UsuarioRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reglas de negocio de las tareas (las mismas que aplica la app al crear, editar y validar). */
@Service
@Transactional
public class TareaService {

    private final TareaRepository repositorio;
    private final SectorRepository sectores;
    private final CultivoRepository cultivos;
    private final UsuarioRepository usuarios;

    public TareaService(TareaRepository repositorio, SectorRepository sectores,
                        CultivoRepository cultivos, UsuarioRepository usuarios) {
        this.repositorio = repositorio;
        this.sectores = sectores;
        this.cultivos = cultivos;
        this.usuarios = usuarios;
    }

    /** Filtra por estado o por estudiante responsable; sin filtros devuelve todas. */
    @Transactional(readOnly = true)
    public List<TareaDto> listar(EstadoTarea estado, Integer responsableId) {
        List<Tarea> lista;
        if (estado != null) {
            lista = repositorio.findByEstadoOrderById(estado);
        } else if (responsableId != null) {
            lista = repositorio.findByResponsable_IdOrderById(responsableId);
        } else {
            lista = repositorio.findAll(Sort.by("id"));
        }
        return lista.stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public TareaDto obtener(Integer id) {
        return aDto(buscar(id));
    }

    public TareaDto crear(TareaDto dto) {
        validarAsignacion(dto);
        Tarea tarea = new Tarea();
        aplicar(tarea, dto);
        tarea.setEstado(EstadoTarea.PENDIENTE); // toda tarea nueva parte pendiente
        if (tarea.getFechaCreacion() == null) {
            tarea.setFechaCreacion(System.currentTimeMillis());
        }
        return aDto(repositorio.save(tarea));
    }

    public TareaDto actualizar(Integer id, TareaDto dto) {
        Tarea tarea = buscar(id);
        validarAsignacion(dto);
        validarEstado(dto);
        aplicar(tarea, dto);
        return aDto(repositorio.save(tarea));
    }

    public void eliminar(Integer id) {
        buscar(id);
        repositorio.deleteById(id);
    }

    private Tarea buscar(Integer id) {
        return repositorio.findById(id).orElseThrow(() -> noEncontrado("No existe la tarea " + id));
    }

    private void validarAsignacion(TareaDto dto) {
        boolean sinResponsable = dto.responsableUsuarioId() == null;
        boolean sinGrupo = dto.grupoAsignado() == null || dto.grupoAsignado().isBlank();
        if (sinResponsable && sinGrupo) {
            throw invalido("Asigna la tarea a un estudiante o a un grupo");
        }
    }

    private void validarEstado(TareaDto dto) {
        if (dto.estado() == EstadoTarea.VALIDADA && dto.validadorId() == null) {
            throw invalido("Una tarea validada debe indicar quién la validó");
        }
        if (dto.estado() == EstadoTarea.RECHAZADA
                && (dto.comentarioDocente() == null || dto.comentarioDocente().isBlank())) {
            throw invalido("Escribe un comentario al rechazar");
        }
    }

    /** Copia los datos del DTO a la entidad y busca las relaciones por id. */
    private void aplicar(Tarea tarea, TareaDto dto) {
        Sector sector = sectorDe(dto.sectorId());
        Cultivo cultivo = cultivoDe(dto.cultivoId(), sector);
        Usuario creadaPor = usuarioDe(dto.creadaPorId());
        Usuario responsable = usuarioOpcional(dto.responsableUsuarioId());
        Usuario realizadaPor = usuarioOpcional(dto.realizadaPorId());
        Usuario validador = usuarioOpcional(dto.validadorId());

        tarea.setTipo(dto.tipo());
        tarea.setDescripcion(dto.descripcion().trim());
        tarea.setInstrucciones(dto.instrucciones().trim());
        tarea.setSector(sector);
        tarea.setCultivo(cultivo);
        tarea.setFechaProgramada(dto.fechaProgramada());
        tarea.setResponsable(responsable);
        tarea.setGrupoAsignado(dto.grupoAsignado() == null || dto.grupoAsignado().isBlank() ? null : dto.grupoAsignado());
        tarea.setCreadaPor(creadaPor);
        tarea.setRealizadaPor(realizadaPor);
        tarea.setValidador(validador);
        tarea.setFechaRealizacion(dto.fechaRealizacion());
        tarea.setFechaValidacion(dto.fechaValidacion());
        tarea.setFotoUri(dto.fotoUri());
        if (dto.fechaCreacion() != null) {
            tarea.setFechaCreacion(dto.fechaCreacion());
        }
        if (dto.estado() != null) {
            tarea.setEstado(dto.estado());
        }
        if (dto.comentarioEstudiante() != null) {
            tarea.setComentarioEstudiante(dto.comentarioEstudiante());
        }
        if (dto.comentarioDocente() != null) {
            tarea.setComentarioDocente(dto.comentarioDocente());
        }
    }

    private Sector sectorDe(Integer id) {
        return sectores.findById(id).orElseThrow(() -> invalido("No existe el sector " + id));
    }

    /** Regla: el cultivo tiene que estar en el mismo sector de la tarea. */
    private Cultivo cultivoDe(Integer id, Sector sector) {
        if (id == null) {
            return null;
        }
        Cultivo cultivo = cultivos.findById(id).orElseThrow(() -> invalido("No existe el cultivo " + id));
        if (!cultivo.getSector().getId().equals(sector.getId())) {
            throw invalido("El cultivo no pertenece a ese sector");
        }
        return cultivo;
    }

    private Usuario usuarioDe(Integer id) {
        return usuarios.findById(id).orElseThrow(() -> invalido("No existe el usuario " + id));
    }

    private Usuario usuarioOpcional(Integer id) {
        return id == null ? null : usuarioDe(id);
    }

    private static Integer idDe(Usuario u) {
        return u == null ? null : u.getId();
    }

    private static Integer idDe(Cultivo c) {
        return c == null ? null : c.getId();
    }

    private TareaDto aDto(Tarea t) {
        return new TareaDto(t.getId(), t.getTipo(), t.getDescripcion(), t.getInstrucciones(),
                t.getSector().getId(), idDe(t.getCultivo()), t.getFechaCreacion(), t.getFechaProgramada(),
                idDe(t.getResponsable()), t.getGrupoAsignado(), idDe(t.getCreadaPor()), t.getEstado(),
                t.getFechaRealizacion(), idDe(t.getRealizadaPor()), t.getFotoUri(),
                t.getComentarioEstudiante(), t.getComentarioDocente(), idDe(t.getValidador()),
                t.getFechaValidacion());
    }
}
