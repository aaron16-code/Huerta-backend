package cl.duoc.huertoescolar.huertoapi.repository;

import cl.duoc.huertoescolar.huertoapi.model.Sector;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Integer> {

    Optional<Sector> findByNombreIgnoreCase(String nombre);
}
