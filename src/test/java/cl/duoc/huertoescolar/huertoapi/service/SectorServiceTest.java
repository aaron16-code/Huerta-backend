package cl.duoc.huertoescolar.huertoapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.huertoescolar.huertoapi.dto.SectorDto;
import cl.duoc.huertoescolar.huertoapi.model.EstadoSector;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
import cl.duoc.huertoescolar.huertoapi.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class SectorServiceTest {

    @Mock private SectorRepository repositorio;
    @Mock private UsuarioRepository usuarios;
    @InjectMocks private SectorService servicio;

    private Sector sector(int id, String nombre) {
        Sector s = new Sector();
        s.setId(id);
        s.setNombre(nombre);
        return s;
    }

    private SectorDto dto(String nombre, Integer responsableId) {
        return new SectorDto(null, nombre, "Hortalizas", "Zona norte", EstadoSector.EN_USO, responsableId);
    }

    @Test
    void crear_con_nombre_repetido_da_error_400() {
        when(repositorio.findByNombreIgnoreCase("Bancal 1")).thenReturn(Optional.of(sector(1, "Bancal 1")));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.crear(dto("Bancal 1", null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(repositorio, never()).save(any());
    }

    @Test
    void crear_ok_guarda_el_sector() {
        when(repositorio.findByNombreIgnoreCase("Bancal 2")).thenReturn(Optional.empty());
        when(repositorio.save(any(Sector.class))).thenAnswer(inv -> inv.getArgument(0));

        SectorDto guardado = servicio.crear(dto("  Bancal 2  ", null));

        assertEquals("Bancal 2", guardado.nombre());
        assertEquals(EstadoSector.EN_USO, guardado.estado());
        assertNull(guardado.responsableId());
    }

    @Test
    void crear_con_responsable_que_no_existe_da_error_400() {
        when(repositorio.findByNombreIgnoreCase("Bancal 2")).thenReturn(Optional.empty());
        when(usuarios.findById(50)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> servicio.crear(dto("Bancal 2", 50)));
    }

    @Test
    void actualizar_puede_conservar_su_propio_nombre() {
        Sector actual = sector(1, "Bancal 1");
        when(repositorio.findById(1)).thenReturn(Optional.of(actual));
        when(repositorio.findByNombreIgnoreCase("Bancal 1")).thenReturn(Optional.of(actual));
        when(repositorio.save(any(Sector.class))).thenAnswer(inv -> inv.getArgument(0));

        SectorDto resultado = servicio.actualizar(1, dto("Bancal 1", null));

        assertEquals(1, resultado.id());
    }

    @Test
    void actualizar_con_el_nombre_de_otro_sector_da_error_400() {
        when(repositorio.findById(1)).thenReturn(Optional.of(sector(1, "Bancal 1")));
        when(repositorio.findByNombreIgnoreCase("Bancal 2")).thenReturn(Optional.of(sector(2, "Bancal 2")));
        assertThrows(ResponseStatusException.class, () -> servicio.actualizar(1, dto("Bancal 2", null)));
    }

    @Test
    void obtener_un_sector_que_no_existe_da_404() {
        when(repositorio.findById(9)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.obtener(9));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void eliminar_un_sector_que_no_existe_da_404_y_no_borra() {
        when(repositorio.findById(9)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> servicio.eliminar(9));
        verify(repositorio, never()).deleteById(any());
    }
}
