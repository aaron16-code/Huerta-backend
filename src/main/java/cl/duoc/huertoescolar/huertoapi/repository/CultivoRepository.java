package cl.duoc.huertoescolar.huertoapi.repository;

import cl.duoc.huertoescolar.huertoapi.model.Cultivo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CultivoRepository extends JpaRepository<Cultivo, Integer> {

    List<Cultivo> findBySector_IdOrderById(Integer sectorId);
}
