
package cl.duoc.huertoescolar.huertoapi.service;

import static cl.duoc.huertoescolar.huertoapi.exception.Errores.invalido;
import static cl.duoc.huertoescolar.huertoapi.exception.Errores.noEncontrado;

import cl.duoc.huertoescolar.huertoapi.dto.TareaDto;
import cl.duoc.huertoescolar.huertoapi.model.Cultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoTarea;
import cl.duoc.huertoescolar.huertoapi.model.Evidencia;
import cl.duoc.huertoescolar.huertoapi.model.ResultadoValidacion;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.model.Tarea;
import cl.duoc.huertoescolar.huertoapi.model.Usuario;
import cl.duoc.huertoescolar.huertoapi.model.Validacion;
import cl.duoc.huertoescolar.huertoapi.repository.CultivoRepository;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
import cl.duoc.huertoescolar.huertoapi.repository.TareaRepository;
import cl.duoc.huertoescolar.huertoapi.repository.UsuarioRepository;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TareaService {

    private final Grupos grupos;
    private final TareaRepository repositorio;
    private final SectorRepository sectores;
    private final CultivoRepository cultivos;
    private final UsuarioRepository usuarios;

    public TareaService(
            TareaRepository repositorio,
            SectorRepository sectores,
            CultivoRepository cultivos,
            UsuarioRepository usuarios,
            Grupos grupos) {

        this.repositorio = repositorio;
        this.grupos = grupos;
        this.sectores = sectores;
        this.cultivos = cultivos;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<TareaDto> listar(
            EstadoTarea estado,
            Integer responsableId) {

        List<Tarea> lista;

        if (estado != null) {
            lista = repositorio.findByEstadoOrderById(estado);
        } else if (responsableId != null) {
            lista = repositorio
                    .findByResponsable_IdOrderById(responsableId);
        } else {
            lista = repositorio.findAll(Sort.by("id"));
        }

        return lista.stream()
                .map(this::aDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TareaDto obtener(Integer id) {
        return aDto(buscar(id));
    }

    public TareaDto crear(TareaDto dto) {

        validarAsignacion(dto);

        Tarea tarea = new Tarea();

        aplicar(tarea, dto);

        tarea.setEstado(EstadoTarea.PENDIENTE);

        if (tarea.getFechaCreacion() == null) {
            tarea.setFechaCreacion(System.currentTimeMillis());
        }

        return aDto(repositorio.save(tarea));
    }

    public TareaDto actualizar(Integer id, TareaDto dto) {

        Tarea tarea = buscar(id);

        validarAsignacion(dto);
        validarEstado(dto);

        EstadoTarea estadoAnterior = tarea.getEstado();

        aplicar(tarea, dto);

        if (dto.estado() == EstadoTarea.VALIDADA
                || dto.estado() == EstadoTarea.RECHAZADA) {

            registrarValidacion(tarea, dto, estadoAnterior);
        }

        return aDto(repositorio.save(tarea));
    }

    public void eliminar(Integer id) {
        buscar(id);
        repositorio.deleteById(id);
    }

    private Tarea buscar(Integer id) {

        return repositorio.findById(id)
                .orElseThrow(() ->
                        noEncontrado("No existe la tarea " + id));
    }

    private void validarAsignacion(TareaDto dto) {

        boolean sinResponsable =
                dto.responsableUsuarioId() == null;

        boolean sinGrupo =
                dto.grupoAsignado() == null
                        || dto.grupoAsignado().isBlank();

        if (sinResponsable == sinGrupo) {
            throw invalido(
                    "Asigna la tarea a un estudiante o a un grupo");
        }
    }

    private void validarEstado(TareaDto dto) {

        if ((dto.estado() == EstadoTarea.VALIDADA
                || dto.estado() == EstadoTarea.RECHAZADA)
                && dto.validadorId() == null) {

            throw invalido(
                    "Una tarea validada o rechazada debe indicar quién la revisó");
        }

        if (dto.estado() == EstadoTarea.RECHAZADA
                && (dto.comentarioDocente() == null
                || dto.comentarioDocente().isBlank())) {

            throw invalido("Escribe un comentario al rechazar");
        }
    }

    private void aplicar(Tarea tarea, TareaDto dto) {

        Sector sector = sectorDe(dto.sectorId());
        Cultivo cultivo = cultivoDe(dto.cultivoId(), sector);

        Usuario creadaPor = usuarioDe(dto.creadaPorId());

        Usuario responsable =
                usuarioOpcional(dto.responsableUsuarioId());

        Usuario realizadaPor =
                usuarioOpcional(dto.realizadaPorId());

        tarea.setTipo(dto.tipo());
        tarea.setDescripcion(dto.descripcion().trim());
        tarea.setInstrucciones(dto.instrucciones().trim());

        tarea.setSector(cultivo == null ? sector : null);
        tarea.setCultivo(cultivo);

        tarea.setFechaProgramada(dto.fechaProgramada());
        tarea.setResponsable(responsable);

        tarea.setGrupoAsignado(
                dto.grupoAsignado() == null
                        || dto.grupoAsignado().isBlank()
                        ? null
                        : grupos.resolver(dto.grupoAsignado())
        );

        tarea.setCreadaPor(creadaPor);
        tarea.setRealizadaPor(realizadaPor);
        tarea.setFechaRealizacion(dto.fechaRealizacion());

        if (dto.fechaCreacion() != null) {
            tarea.setFechaCreacion(dto.fechaCreacion());
        }

        if (dto.estado() != null) {
            tarea.setEstado(dto.estado());
        }

        registrarEvidencia(tarea, dto, realizadaPor);
    }

    /**
     * Guarda fotografías y comentarios únicamente en evidencias.
     * Conserva el historial y evita duplicados consecutivos.
     */
    private void registrarEvidencia(
            Tarea tarea,
            TareaDto dto,
            Usuario realizadaPor) {

        Evidencia anterior = tarea.ultimaEvidencia();

        String foto = dto.fotoUri();
        String comentario = dto.comentarioEstudiante();

        // Si no se proporciona un valor, conserva el anterior.
        if (anterior != null) {

            if (foto == null) {
                foto = anterior.getFotoUri();
            }

            if (comentario == null) {
                comentario = anterior.getComentario();
            }
        }

        // Normaliza valores vacíos.
        if (foto != null && foto.isBlank()) {
            foto = null;
        }

        if (comentario != null && comentario.isBlank()) {
            comentario = null;
        }

        boolean tieneFoto = foto != null;
        boolean tieneComentario = comentario != null;

        // No permite evidencias completamente vacías.
        if (!tieneFoto && !tieneComentario) {

            if (anterior != null) {
                throw invalido(
                        "La evidencia debe contener una fotografía o un comentario");
            }

            return;
        }

        // Evita registrar nuevamente una evidencia idéntica.
        if (anterior != null
                && Objects.equals(anterior.getFotoUri(), foto)
                && Objects.equals(anterior.getComentario(), comentario)) {

            return;
        }

        Evidencia evidencia = new Evidencia();

        evidencia.setTarea(tarea);

        evidencia.setUsuario(
                realizadaPor != null
                        ? realizadaPor
                        : anterior == null
                          ? null
                          : anterior.getUsuario()
        );

        evidencia.setFotoUri(foto);
        evidencia.setComentario(comentario);
        evidencia.setFechaRegistro(System.currentTimeMillis());

        tarea.getEvidencias().add(evidencia);
    }

    /**
     * Registra el historial de validaciones del docente.
     */
    private void registrarValidacion(
            Tarea tarea,
            TareaDto dto,
            EstadoTarea estadoAnterior) {

        Validacion anterior = tarea.ultimaValidacion();

        ResultadoValidacion resultado =
                ResultadoValidacion.valueOf(dto.estado().name());

        boolean cambio =
                anterior == null
                        || estadoAnterior != dto.estado()
                        || anterior.getResultado() != resultado
                        || !Objects.equals(
                        anterior.getComentario(),
                        dto.comentarioDocente())
                        || !Objects.equals(
                        anterior.getValidador().getId(),
                        dto.validadorId());

        if (cambio) {

            Validacion validacion = new Validacion();

            validacion.setTarea(tarea);
            validacion.setValidador(usuarioDe(dto.validadorId()));
            validacion.setResultado(resultado);
            validacion.setComentario(dto.comentarioDocente());

            validacion.setFechaValidacion(
                    dto.fechaValidacion() == null
                            ? System.currentTimeMillis()
                            : dto.fechaValidacion()
            );

            tarea.getValidaciones().add(validacion);
        }
    }

    private Sector sectorDe(Integer id) {

        return sectores.findById(id)
                .orElseThrow(() ->
                        invalido("No existe el sector " + id));
    }

    private Cultivo cultivoDe(Integer id, Sector sector) {

        if (id == null) {
            return null;
        }

        Cultivo cultivo = cultivos.findById(id)
                .orElseThrow(() ->
                        invalido("No existe el cultivo " + id));

        if (!cultivo.getSector().getId().equals(sector.getId())) {
            throw invalido("El cultivo no pertenece a ese sector");
        }

        return cultivo;
    }

    private Usuario usuarioDe(Integer id) {

        return usuarios.findById(id)
                .orElseThrow(() ->
                        invalido("No existe el usuario " + id));
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

    /**
     * Construye el DTO recuperando los datos normalizados.
     * Mantiene la estructura que consume Android.
     */
    private TareaDto aDto(Tarea t) {

        Evidencia evidencia = t.ultimaEvidencia();
        Validacion validacion = t.ultimaValidacion();

        return new TareaDto(

                t.getId(),
                t.getTipo(),
                t.getDescripcion(),
                t.getInstrucciones(),

                t.sectorEfectivo().getId(),
                idDe(t.getCultivo()),

                t.getFechaCreacion(),
                t.getFechaProgramada(),

                idDe(t.getResponsable()),
                Grupos.nombre(t.getGrupoAsignado()),
                idDe(t.getCreadaPor()),

                t.getEstado(),
                t.getFechaRealizacion(),
                idDe(t.getRealizadaPor()),

                evidencia == null
                        ? null
                        : evidencia.getFotoUri(),

                evidencia == null
                        || evidencia.getComentario() == null
                        ? ""
                        : evidencia.getComentario(),

                validacion == null
                        ? ""
                        : validacion.getComentario(),

                validacion == null
                        ? null
                        : idDe(validacion.getValidador()),

                validacion == null
                        ? null
                        : validacion.getFechaValidacion()
        );
    }
}
