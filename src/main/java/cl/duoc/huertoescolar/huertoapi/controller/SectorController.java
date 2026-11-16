package cl.duoc.huertoescolar.huertoapi.controller;

import cl.duoc.huertoescolar.huertoapi.dto.SectorDto;
import cl.duoc.huertoescolar.huertoapi.service.SectorService;
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
@RequestMapping("/api/sectores")
public class SectorController {

    private final SectorService servicio;

    public SectorController(SectorService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<SectorDto> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public SectorDto obtener(@PathVariable Integer id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SectorDto crear(@Valid @RequestBody SectorDto dto) {
        return servicio.crear(dto);
    }

    @PutMapping("/{id}")
    public SectorDto actualizar(@PathVariable Integer id, @Valid @RequestBody SectorDto dto) {
        return servicio.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
    }
}
