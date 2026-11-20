package cl.duoc.huertoescolar.huertoapi.service;

import static cl.duoc.huertoescolar.huertoapi.exception.Errores.invalido;
import static cl.duoc.huertoescolar.huertoapi.exception.Errores.noEncontrado;

import cl.duoc.huertoescolar.huertoapi.dto.UsuarioDto;
import cl.duoc.huertoescolar.huertoapi.model.Rol;
import cl.duoc.huertoescolar.huertoapi.model.Usuario;
import cl.duoc.huertoescolar.huertoapi.repository.UsuarioRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final Grupos grupos;
    private final UsuarioRepository repositorio;

    public UsuarioService(UsuarioRepository repositorio, Grupos grupos) {
        this.repositorio = repositorio;
        this.grupos = grupos;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> listar() {
        return repositorio.findAll(Sort.by("id")).stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDto obtener(Integer id) {
        return aDto(buscar(id));
    }

    public UsuarioDto crear(UsuarioDto dto) {
        validar(dto);
        Usuario usuario = new Usuario();
        aplicar(usuario, dto);
        return aDto(repositorio.save(usuario));
    }

    public UsuarioDto actualizar(Integer id, UsuarioDto dto) {
        Usuario usuario = buscar(id);
        validar(dto);
        aplicar(usuario, dto);
        return aDto(repositorio.save(usuario));
    }

    public void eliminar(Integer id) {
        buscar(id);
        repositorio.deleteById(id);
    }

    private Usuario buscar(Integer id) {
        return repositorio.findById(id).orElseThrow(() -> noEncontrado("No existe el usuario " + id));
    }

    private void validar(UsuarioDto dto) {
        boolean sinGrupo = dto.grupo() == null || dto.grupo().isBlank();
        if (dto.rol() == Rol.ESTUDIANTE && sinGrupo) {
            throw invalido("Un estudiante debe pertenecer a un grupo");
        }
    }

    private void aplicar(Usuario usuario, UsuarioDto dto) {
        usuario.setNombre(dto.nombre().trim());
        usuario.setRol(dto.rol());
        usuario.setGrupo(dto.grupo()==null || dto.grupo().isBlank()?null:grupos.resolver(dto.grupo()));
    }

    private UsuarioDto aDto(Usuario u) {
        return new UsuarioDto(u.getId(), u.getNombre(), u.getRol(), u.getGrupo()==null?"":u.getGrupo().getNombre());
    }
}
