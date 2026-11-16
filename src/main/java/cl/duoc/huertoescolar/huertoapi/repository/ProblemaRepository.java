package cl.duoc.huertoescolar.huertoapi.repository;

import cl.duoc.huertoescolar.huertoapi.model.Problema;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemaRepository extends JpaRepository<Problema, Integer> {

    List<Problema> findByReportadoPor_IdOrderById(Integer usuarioId);
}
