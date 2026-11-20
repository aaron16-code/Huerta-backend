package cl.duoc.huertoescolar.huertoapi.repository;
import cl.duoc.huertoescolar.huertoapi.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GrupoRepository extends JpaRepository<Grupo,Integer> {
java.util.Optional<Grupo> findByNombreIgnoreCase(String nombre);
}
