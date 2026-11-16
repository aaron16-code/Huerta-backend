package cl.duoc.huertoescolar.huertoapi.repository;

import cl.duoc.huertoescolar.huertoapi.model.EstadoTarea;
import cl.duoc.huertoescolar.huertoapi.model.Tarea;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    List<Tarea> findByEstadoOrderById(EstadoTarea estado);

    List<Tarea> findByResponsable_IdOrderById(Integer usuarioId);
}
