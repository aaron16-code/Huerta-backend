package cl.duoc.huertoescolar.huertoapi.service;

import static cl.duoc.huertoescolar.huertoapi.exception.Errores.invalido;
import static cl.duoc.huertoescolar.huertoapi.exception.Errores.noEncontrado;

import cl.duoc.huertoescolar.huertoapi.dto.SectorDto;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.model.Usuario;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
import cl.duoc.huertoescolar.huertoapi.repository.UsuarioRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectorService {

    private final SectorRepository repositorio;
    private final UsuarioRepository usuarios;

    public SectorService(SectorRepository repositorio, UsuarioRepository usuarios) {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<SectorDto> listar() {
        return repositorio.findAll(Sort.by("id")).stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public SectorDto obtener(Integer id) {
        return aDto(buscar(id));
    }

    public SectorDto crear(SectorDto dto) {
        validarNombreUnico(dto.nombre(), null);
        Sector sector = new Sector();
        aplicar(sector, dto);
        return aDto(repositorio.save(sector));
    }

    public SectorDto actualizar(Integer id, SectorDto dto) {
        Sector sector = buscar(id);
        validarNombreUnico(dto.nombre(), id);
        aplicar(sector, dto);
        return aDto(repositorio.save(sector));
    }

    public void eliminar(Integer id) {
        buscar(id);
        repositorio.deleteById(id);
    }

    private Sector buscar(Integer id) {
        return repositorio.findById(id).orElseThrow(() -> noEncontrado("No existe el sector " + id));
    }

    /** Regla: no puede haber dos sectores con el mismo nombre. */
    private void validarNombreUnico(String nombre, Integer idActual) {
        repositorio.findByNombreIgnoreCase(nombre.trim()).ifPresent(otro -> {
            if (!otro.getId().equals(idActual)) {
                throw invalido("Ya existe un sector con ese nombre");
            }
        });
    }

    private void aplicar(Sector sector, SectorDto dto) {
        sector.setNombre(dto.nombre().trim());
        sector.setDescripcion(dto.descripcion() == null ? "" : dto.descripcion());
        sector.setUbicacion(dto.ubicacion() == null ? "" : dto.ubicacion());
        sector.setEstado(dto.estado());
        Usuario responsable = null;
        if (dto.responsableId() != null) {
            responsable = usuarios.findById(dto.responsableId())
                    .orElseThrow(() -> invalido("No existe el usuario " + dto.responsableId()));
        }
        sector.setResponsable(responsable);
    }

    private SectorDto aDto(Sector s) {
        Integer responsableId = s.getResponsable() == null ? null : s.getResponsable().getId();
        return new SectorDto(s.getId(), s.getNombre(), s.getDescripcion(), s.getUbicacion(), s.getEstado(), responsableId);
    }
}
