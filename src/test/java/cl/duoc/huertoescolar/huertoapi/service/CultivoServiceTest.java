package cl.duoc.huertoescolar.huertoapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.huertoescolar.huertoapi.dto.CultivoDto;
import cl.duoc.huertoescolar.huertoapi.model.Cultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoCultivo;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.repository.CultivoRepository;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CultivoServiceTest {

    @Mock private CultivoRepository repositorio;
    @Mock private SectorRepository sectores;
    @InjectMocks private CultivoService servicio;

    private Sector sector(int id) {
        Sector s = new Sector();
        s.setId(id);
        return s;
    }

    private CultivoDto dto(Integer sectorId, EstadoCultivo estado) {
        return new CultivoDto(null, "Lechugas", "Prueba", sectorId, "01/10/2026", "30/11/2026", 2, estado, null, null);
    }

    private Cultivo existente() {
        Cultivo c = new Cultivo();
        c.setId(1);
        c.setNombre("Lechugas");
        c.setSector(sector(1));
        return c;
    }

    @Test
    void crear_con_sector_que_no_existe_da_error_400() {
        when(sectores.findById(9)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.crear(dto(9, null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(repositorio, never()).save(any());
    }

    @Test
    void crear_sin_estado_parte_planificado() {
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(repositorio.save(any(Cultivo.class))).thenAnswer(inv -> inv.getArgument(0));

        CultivoDto guardado = servicio.crear(dto(1, null));

        assertEquals(EstadoCultivo.PLANIFICADO, guardado.estado());
        assertEquals(1, guardado.sectorId());
        assertEquals(2, guardado.frecuenciaRiegoDias());
    }

    @Test
    void actualizar_cambia_el_estado_y_conserva_el_id() {
        when(repositorio.findById(1)).thenReturn(Optional.of(existente()));
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(repositorio.save(any(Cultivo.class))).thenAnswer(inv -> inv.getArgument(0));

        CultivoDto resultado = servicio.actualizar(1, dto(1, EstadoCultivo.COSECHADO));

        assertEquals(1, resultado.id());
        assertEquals(EstadoCultivo.COSECHADO, resultado.estado());
    }

    @Test
    void obtener_un_cultivo_que_no_existe_da_404() {
        when(repositorio.findById(4)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> servicio.obtener(4));
    }

    @Test
    void listar_por_sector_filtra_por_sector() {
        when(repositorio.findBySector_IdOrderById(1)).thenReturn(List.of(existente()));
        assertEquals(1, servicio.listar(1).size());
        verify(repositorio, never()).findAll(any(Sort.class));
    }

    @Test
    void listar_sin_filtro_trae_todos() {
        when(repositorio.findAll(any(Sort.class))).thenReturn(List.of(existente(), existente()));
        assertEquals(2, servicio.listar(null).size());
    }

    @Test
    void eliminar_un_cultivo_que_no_existe_da_404_y_no_borra() {
        when(repositorio.findById(8)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> servicio.eliminar(8));
        verify(repositorio, never()).deleteById(any());
    }
}
