package cl.duoc.huertoescolar.huertoapi.controller;

import cl.duoc.huertoescolar.huertoapi.dto.ProblemaDto;
import cl.duoc.huertoescolar.huertoapi.service.ProblemaService;
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
@RequestMapping("/api/problemas")
public class ProblemaController {

    private final ProblemaService servicio;

    public ProblemaController(ProblemaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ProblemaDto> listar(@RequestParam(required = false) Integer reportadoPorId) {
        return servicio.listar(reportadoPorId);
    }

    @GetMapping("/{id}")
    public ProblemaDto obtener(@PathVariable Integer id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProblemaDto crear(@Valid @RequestBody ProblemaDto dto) {
        return servicio.crear(dto);
    }

    @PutMapping("/{id}")
    public ProblemaDto actualizar(@PathVariable Integer id, @Valid @RequestBody ProblemaDto dto) {
        return servicio.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
    }
}
