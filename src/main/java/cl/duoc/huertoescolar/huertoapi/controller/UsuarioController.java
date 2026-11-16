package cl.duoc.huertoescolar.huertoapi.controller;

import cl.duoc.huertoescolar.huertoapi.dto.UsuarioDto;
import cl.duoc.huertoescolar.huertoapi.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<UsuarioDto> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public UsuarioDto obtener(@PathVariable Integer id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto crear(@Valid @RequestBody UsuarioDto dto) {
        return servicio.crear(dto);
    }

    @PutMapping("/{id}")
    public UsuarioDto actualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioDto dto) {
        return servicio.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
    }
}
