package cl.duoc.huertoescolar.huertoapi.controller;

import cl.duoc.huertoescolar.huertoapi.dto.TareaDto;
import cl.duoc.huertoescolar.huertoapi.model.EstadoTarea;
import cl.duoc.huertoescolar.huertoapi.service.TareaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService servicio;

    public TareaController(TareaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<TareaDto> listar(@RequestParam(required = false) EstadoTarea estado, @RequestParam(required = false) Integer responsableId) {
        return servicio.listar(estado, responsableId);
    }

    @GetMapping("/{id}")
    public TareaDto obtener(@PathVariable Integer id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TareaDto crear(@Valid @RequestBody TareaDto dto) {
        return servicio.crear(dto);
    }

    @PutMapping("/{id}")
    public TareaDto actualizar(@PathVariable Integer id, @Valid @RequestBody TareaDto dto) {
        return servicio.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
    }
}
