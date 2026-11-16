package cl.duoc.huertoescolar.huertoapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.huertoescolar.huertoapi.dto.TareaDto;
import cl.duoc.huertoescolar.huertoapi.model.Cultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoTarea;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.model.Tarea;
import cl.duoc.huertoescolar.huertoapi.model.TipoTarea;
import cl.duoc.huertoescolar.huertoapi.model.Usuario;
import cl.duoc.huertoescolar.huertoapi.repository.CultivoRepository;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
import cl.duoc.huertoescolar.huertoapi.repository.TareaRepository;
import cl.duoc.huertoescolar.huertoapi.repository.UsuarioRepository;
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
class TareaServiceTest {

    @Mock private TareaRepository repositorio;
    @Mock private SectorRepository sectores;
    @Mock private CultivoRepository cultivos;
    @Mock private UsuarioRepository usuarios;
    @InjectMocks private TareaService servicio;

    private Sector sector(int id) {
        Sector s = new Sector();
        s.setId(id);
        return s;
    }

    private Usuario usuario(int id) {
        Usuario u = new Usuario();
        u.setId(id);
        return u;
    }

    private TareaDto dto(Integer sectorId, Integer cultivoId, Integer responsable, String grupo,
                         EstadoTarea estado, Integer validadorId, String comentarioDocente) {
        return new TareaDto(null, TipoTarea.RIEGO, "Regar la huerta", "Usar la regadera", sectorId, cultivoId,
                null, System.currentTimeMillis() + 86_400_000L, responsable, grupo, 1, estado,
                null, null, null, null, comentarioDocente, validadorId, null);
    }

    private TareaDto dtoValido() {
        return dto(1, null, null, "Taller Huerto A", null, null, null);
    }

    private Tarea tareaExistente() {
        Tarea t = new Tarea();
        t.setId(3);
        t.setSector(sector(1));
        t.setCreadaPor(usuario(1));
        t.setTipo(TipoTarea.RIEGO);
        t.setDescripcion("Regar");
        t.setInstrucciones("Con regadera");
        t.setFechaProgramada(1L);
        return t;
    }

    @Test
    void crear_sin_estudiante_ni_grupo_da_error_400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.crear(dto(1, null, null, null, null, null, null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(repositorio, never()).save(any());
    }

    @Test
    void crear_con_grupo_en_blanco_da_error_400() {
        assertThrows(ResponseStatusException.class,
                () -> servicio.crear(dto(1, null, null, "   ", null, null, null)));
    }

    @Test
    void crear_con_sector_que_no_existe_da_error_400() {
        when(sectores.findById(99)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.crear(dto(99, null, null, "Taller Huerto A", null, null, null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void crear_con_cultivo_de_otro_sector_da_error_400() {
        Cultivo cultivo = new Cultivo();
        cultivo.setId(5);
        cultivo.setSector(sector(2));
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(cultivos.findById(5)).thenReturn(Optional.of(cultivo));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.crear(dto(1, 5, null, "Taller Huerto A", null, null, null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void crear_con_usuario_creador_que_no_existe_da_error_400() {
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(usuarios.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> servicio.crear(dtoValido()));
    }

    @Test
    void crear_ok_parte_pendiente_y_con_fecha_de_creacion() {
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(usuarios.findById(1)).thenReturn(Optional.of(usuario(1)));
        when(repositorio.save(any(Tarea.class))).thenAnswer(inv -> inv.getArgument(0));

        TareaDto guardada = servicio.crear(dto(1, null, null, "Taller Huerto A", EstadoTarea.VALIDADA, null, null));

        assertEquals(EstadoTarea.PENDIENTE, guardada.estado());
        assertNotNull(guardada.fechaCreacion());
        assertEquals(1, guardada.sectorId());
        assertEquals(1, guardada.creadaPorId());
        assertEquals("Taller Huerto A", guardada.grupoAsignado());
    }

    @Test
    void obtener_una_tarea_que_no_existe_da_404() {
        when(repositorio.findById(5)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.obtener(5));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void actualizar_rechazar_sin_comentario_da_error_400() {
        when(repositorio.findById(3)).thenReturn(Optional.of(tareaExistente()));
        TareaDto cambios = dto(1, null, null, "Taller Huerto A", EstadoTarea.RECHAZADA, null, "  ");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.actualizar(3, cambios));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(repositorio, never()).save(any());
    }

    @Test
    void actualizar_validar_sin_validador_da_error_400() {
        when(repositorio.findById(3)).thenReturn(Optional.of(tareaExistente()));
        TareaDto cambios = dto(1, null, null, "Taller Huerto A", EstadoTarea.VALIDADA, null, null);
        assertThrows(ResponseStatusException.class, () -> servicio.actualizar(3, cambios));
    }

    @Test
    void actualizar_rechazar_con_comentario_se_guarda() {
        when(repositorio.findById(3)).thenReturn(Optional.of(tareaExistente()));
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(usuarios.findById(1)).thenReturn(Optional.of(usuario(1)));
        when(repositorio.save(any(Tarea.class))).thenAnswer(inv -> inv.getArgument(0));
        TareaDto cambios = dto(1, null, null, "Taller Huerto A", EstadoTarea.RECHAZADA, null, "Falta la foto");

        TareaDto resultado = servicio.actualizar(3, cambios);

        assertEquals(3, resultado.id());
        assertEquals(EstadoTarea.RECHAZADA, resultado.estado());
        assertEquals("Falta la foto", resultado.comentarioDocente());
    }

    @Test
    void eliminar_una_tarea_que_no_existe_da_404_y_no_borra() {
        when(repositorio.findById(8)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> servicio.eliminar(8));
        verify(repositorio, never()).deleteById(any());
    }

    @Test
    void eliminar_una_tarea_existente_la_borra() {
        when(repositorio.findById(3)).thenReturn(Optional.of(tareaExistente()));
        servicio.eliminar(3);
        verify(repositorio).deleteById(3);
    }

    @Test
    void listar_sin_filtros_trae_todas() {
        when(repositorio.findAll(any(Sort.class))).thenReturn(List.of(tareaExistente(), tareaExistente()));
        assertEquals(2, servicio.listar(null, null).size());
    }

    @Test
    void listar_por_estado_filtra_por_estado() {
        when(repositorio.findByEstadoOrderById(EstadoTarea.VALIDADA)).thenReturn(List.of(tareaExistente()));
        assertEquals(1, servicio.listar(EstadoTarea.VALIDADA, null).size());
        verify(repositorio, never()).findAll(any(Sort.class));
    }

    @Test
    void listar_por_estudiante_filtra_por_responsable() {
        when(repositorio.findByResponsable_IdOrderById(2)).thenReturn(List.of(tareaExistente()));
        assertEquals(1, servicio.listar(null, 2).size());
    }
}
