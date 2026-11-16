package cl.duoc.huertoescolar.huertoapi.controller;

import cl.duoc.huertoescolar.huertoapi.dto.CultivoDto;
import cl.duoc.huertoescolar.huertoapi.service.CultivoService;
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
@RequestMapping("/api/cultivos")
public class CultivoController {

    private final CultivoService servicio;

    public CultivoController(CultivoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<CultivoDto> listar(@RequestParam(required = false) Integer sectorId) {
        return servicio.listar(sectorId);
    }

    @GetMapping("/{id}")
    public CultivoDto obtener(@PathVariable Integer id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CultivoDto crear(@Valid @RequestBody CultivoDto dto) {
        return servicio.crear(dto);
    }

    @PutMapping("/{id}")
    public CultivoDto actualizar(@PathVariable Integer id, @Valid @RequestBody CultivoDto dto) {
        return servicio.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
    }
}
