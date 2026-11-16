package cl.duoc.huertoescolar.huertoapi.service;

import static cl.duoc.huertoescolar.huertoapi.exception.Errores.invalido;
import static cl.duoc.huertoescolar.huertoapi.exception.Errores.noEncontrado;

import cl.duoc.huertoescolar.huertoapi.dto.CultivoDto;
import cl.duoc.huertoescolar.huertoapi.model.Cultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoCultivo;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.repository.CultivoRepository;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CultivoService {

    private final CultivoRepository repositorio;
    private final SectorRepository sectores;

    public CultivoService(CultivoRepository repositorio, SectorRepository sectores) {
        this.repositorio = repositorio;
        this.sectores = sectores;
    }

    /** Si se indica sectorId, devuelve solo los cultivos de ese sector. */
    @Transactional(readOnly = true)
    public List<CultivoDto> listar(Integer sectorId) {
        List<Cultivo> lista = sectorId == null
                ? repositorio.findAll(Sort.by("id"))
                : repositorio.findBySector_IdOrderById(sectorId);
        return lista.stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public CultivoDto obtener(Integer id) {
        return aDto(buscar(id));
    }

    public CultivoDto crear(CultivoDto dto) {
        Cultivo cultivo = new Cultivo();
        aplicar(cultivo, dto);
        return aDto(repositorio.save(cultivo));
    }

    public CultivoDto actualizar(Integer id, CultivoDto dto) {
        Cultivo cultivo = buscar(id);
        aplicar(cultivo, dto);
        return aDto(repositorio.save(cultivo));
    }

    public void eliminar(Integer id) {
        buscar(id);
        repositorio.deleteById(id);
    }

    private Cultivo buscar(Integer id) {
        return repositorio.findById(id).orElseThrow(() -> noEncontrado("No existe el cultivo " + id));
    }

    private void aplicar(Cultivo cultivo, CultivoDto dto) {
        Sector sector = sectores.findById(dto.sectorId())
                .orElseThrow(() -> invalido("No existe el sector " + dto.sectorId()));
        cultivo.setSector(sector);
        cultivo.setNombre(dto.nombre().trim());
        cultivo.setDescripcion(dto.descripcion() == null ? "" : dto.descripcion());
        cultivo.setFechaSiembra(dto.fechaSiembra() == null ? "" : dto.fechaSiembra());
        cultivo.setFechaCosechaEstimada(dto.fechaCosechaEstimada() == null ? "" : dto.fechaCosechaEstimada());
        cultivo.setFrecuenciaRiegoDias(dto.frecuenciaRiegoDias());
        cultivo.setEstado(dto.estado() == null ? EstadoCultivo.PLANIFICADO : dto.estado());
        cultivo.setObservaciones(dto.observaciones() == null ? "" : dto.observaciones());
        cultivo.setFotoUri(dto.fotoUri());
    }

    private CultivoDto aDto(Cultivo c) {
        return new CultivoDto(c.getId(), c.getNombre(), c.getDescripcion(), c.getSector().getId(),
                c.getFechaSiembra(), c.getFechaCosechaEstimada(), c.getFrecuenciaRiegoDias(),
                c.getEstado(), c.getObservaciones(), c.getFotoUri());
    }
}
