package cl.duoc.huertoescolar.huertoapi.service;

import static cl.duoc.huertoescolar.huertoapi.exception.Errores.invalido;
import static cl.duoc.huertoescolar.huertoapi.exception.Errores.noEncontrado;

import cl.duoc.huertoescolar.huertoapi.dto.ProblemaDto;
import cl.duoc.huertoescolar.huertoapi.model.Cultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoReporte;
import cl.duoc.huertoescolar.huertoapi.model.Problema;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.model.Usuario;
import cl.duoc.huertoescolar.huertoapi.repository.CultivoRepository;
import cl.duoc.huertoescolar.huertoapi.repository.ProblemaRepository;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
import cl.duoc.huertoescolar.huertoapi.repository.UsuarioRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProblemaService {

    private final ProblemaRepository repositorio;
    private final SectorRepository sectores;
    private final CultivoRepository cultivos;
    private final UsuarioRepository usuarios;

    public ProblemaService(ProblemaRepository repositorio, SectorRepository sectores,
                           CultivoRepository cultivos, UsuarioRepository usuarios) {
        this.repositorio = repositorio;
        this.sectores = sectores;
        this.cultivos = cultivos;
        this.usuarios = usuarios;
    }

    /** Si se indica reportadoPorId, devuelve solo los problemas de ese estudiante. */
    @Transactional(readOnly = true)
    public List<ProblemaDto> listar(Integer reportadoPorId) {
        List<Problema> lista = reportadoPorId == null
                ? repositorio.findAll(Sort.by("id"))
                : repositorio.findByReportadoPor_IdOrderById(reportadoPorId);
        return lista.stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public ProblemaDto obtener(Integer id) {
        return aDto(buscar(id));
    }

    public ProblemaDto crear(ProblemaDto dto) {
        Problema problema = new Problema();
        aplicar(problema, dto);
        problema.setEstado(EstadoReporte.INFORMADO); // todo reporte nuevo parte informado
        problema.setFechaCierre(null);
        if (problema.getFechaReporte() == null) {
            problema.setFechaReporte(System.currentTimeMillis());
        }
        return aDto(repositorio.save(problema));
    }

    public ProblemaDto actualizar(Integer id, ProblemaDto dto) {
        Problema problema = buscar(id);
        aplicar(problema, dto);
        if (problema.getEstado() == EstadoReporte.CERRADO) {
            if (problema.getFechaCierre() == null) {
                problema.setFechaCierre(System.currentTimeMillis());
            }
        } else {
            problema.setFechaCierre(null);
        }
        return aDto(repositorio.save(problema));
    }

    public void eliminar(Integer id) {
        buscar(id);
        repositorio.deleteById(id);
    }

    private Problema buscar(Integer id) {
        return repositorio.findById(id).orElseThrow(() -> noEncontrado("No existe el problema " + id));
    }

    private void aplicar(Problema problema, ProblemaDto dto) {
        Sector sector = sectores.findById(dto.sectorId())
                .orElseThrow(() -> invalido("No existe el sector " + dto.sectorId()));
        Cultivo cultivo = null;
        if (dto.cultivoId() != null) {
            cultivo = cultivos.findById(dto.cultivoId())
                    .orElseThrow(() -> invalido("No existe el cultivo " + dto.cultivoId()));
            // Regla: el cultivo tiene que estar en el mismo sector del problema
            if (!cultivo.getSector().getId().equals(sector.getId())) {
                throw invalido("El cultivo no pertenece a ese sector");
            }
        }
        Usuario reportadoPor = usuarios.findById(dto.reportadoPorId())
                .orElseThrow(() -> invalido("No existe el usuario " + dto.reportadoPorId()));

        problema.setSector(cultivo == null ? sector : null);
        problema.setCultivo(cultivo);
        problema.setReportadoPor(reportadoPor);
        problema.setTipo(dto.tipo());
        problema.setDescripcion(dto.descripcion().trim());
        problema.setFotoUri(dto.fotoUri());
        problema.setFechaReporte(dto.fechaReporte() != null ? dto.fechaReporte() : problema.getFechaReporte());
        problema.setFechaCierre(dto.fechaCierre());
        if (dto.estado() != null) {
            problema.setEstado(dto.estado());
        }
        if (dto.comentarioDocente() != null) {
            problema.setComentarioDocente(dto.comentarioDocente());
        }
    }

    private ProblemaDto aDto(Problema p) {
        Integer cultivoId = p.getCultivo() == null ? null : p.getCultivo().getId();
        return new ProblemaDto(p.getId(), p.sectorEfectivo().getId(), cultivoId, p.getTipo(), p.getDescripcion(),
                p.getFechaReporte(), p.getReportadoPor().getId(), p.getFotoUri(), p.getEstado(),
                p.getComentarioDocente(), p.getFechaCierre());
    }
}
