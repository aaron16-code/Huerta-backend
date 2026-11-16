package cl.duoc.huertoescolar.huertoapi.repository;

import cl.duoc.huertoescolar.huertoapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
