package cl.duoc.huertoescolar.huertoapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.huertoescolar.huertoapi.dto.ProblemaDto;
import cl.duoc.huertoescolar.huertoapi.model.Cultivo;
import cl.duoc.huertoescolar.huertoapi.model.EstadoReporte;
import cl.duoc.huertoescolar.huertoapi.model.Problema;
import cl.duoc.huertoescolar.huertoapi.model.Sector;
import cl.duoc.huertoescolar.huertoapi.model.TipoProblema;
import cl.duoc.huertoescolar.huertoapi.model.Usuario;
import cl.duoc.huertoescolar.huertoapi.repository.CultivoRepository;
import cl.duoc.huertoescolar.huertoapi.repository.ProblemaRepository;
import cl.duoc.huertoescolar.huertoapi.repository.SectorRepository;
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
class ProblemaServiceTest {

    @Mock private ProblemaRepository repositorio;
    @Mock private SectorRepository sectores;
    @Mock private CultivoRepository cultivos;
    @Mock private UsuarioRepository usuarios;
    @InjectMocks private ProblemaService servicio;

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

    private ProblemaDto dto(Integer cultivoId, EstadoReporte estado, Long fechaCierre) {
        return new ProblemaDto(null, 1, cultivoId, TipoProblema.FALTA_AGUA, "La tierra está seca",
                null, 2, null, estado, null, fechaCierre);
    }

    private Problema existente() {
        Problema p = new Problema();
        p.setId(1);
        p.setSector(sector(1));
        p.setReportadoPor(usuario(2));
        p.setTipo(TipoProblema.FALTA_AGUA);
        p.setDescripcion("Seca");
        return p;
    }

    @Test
    void crear_siempre_parte_informado_y_con_fecha() {
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(usuarios.findById(2)).thenReturn(Optional.of(usuario(2)));
        when(repositorio.save(any(Problema.class))).thenAnswer(inv -> inv.getArgument(0));

        ProblemaDto guardado = servicio.crear(dto(null, EstadoReporte.CERRADO, 99L));

        assertEquals(EstadoReporte.INFORMADO, guardado.estado());
        assertNotNull(guardado.fechaReporte());
        assertNull(guardado.fechaCierre());
        assertEquals(2, guardado.reportadoPorId());
    }

    @Test
    void crear_con_sector_que_no_existe_da_error_400() {
        when(sectores.findById(1)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.crear(dto(null, null, null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void crear_con_cultivo_de_otro_sector_da_error_400() {
        Cultivo cultivo = new Cultivo();
        cultivo.setId(7);
        cultivo.setSector(sector(2));
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(cultivos.findById(7)).thenReturn(Optional.of(cultivo));
        assertThrows(ResponseStatusException.class, () -> servicio.crear(dto(7, null, null)));
    }

    @Test
    void crear_con_usuario_que_no_existe_da_error_400() {
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(usuarios.findById(2)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> servicio.crear(dto(null, null, null)));
    }

    @Test
    void obtener_un_problema_que_no_existe_da_404() {
        when(repositorio.findById(4)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.obtener(4));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void cerrar_un_problema_pone_la_fecha_de_cierre() {
        when(repositorio.findById(1)).thenReturn(Optional.of(existente()));
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(usuarios.findById(2)).thenReturn(Optional.of(usuario(2)));
        when(repositorio.save(any(Problema.class))).thenAnswer(inv -> inv.getArgument(0));

        ProblemaDto resultado = servicio.actualizar(1, dto(null, EstadoReporte.CERRADO, null));

        assertEquals(1, resultado.id());
        assertEquals(EstadoReporte.CERRADO, resultado.estado());
        assertNotNull(resultado.fechaCierre());
    }

    @Test
    void reabrir_un_problema_borra_la_fecha_de_cierre() {
        when(repositorio.findById(1)).thenReturn(Optional.of(existente()));
        when(sectores.findById(1)).thenReturn(Optional.of(sector(1)));
        when(usuarios.findById(2)).thenReturn(Optional.of(usuario(2)));
        when(repositorio.save(any(Problema.class))).thenAnswer(inv -> inv.getArgument(0));

        ProblemaDto resultado = servicio.actualizar(1, dto(null, EstadoReporte.EN_REVISION, 123L));

        assertNull(resultado.fechaCierre());
    }

    @Test
    void eliminar_un_problema_que_no_existe_da_404_y_no_borra() {
        when(repositorio.findById(9)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> servicio.eliminar(9));
        verify(repositorio, never()).deleteById(any());
    }

    @Test
    void listar_sin_filtro_trae_todos() {
        when(repositorio.findAll(any(Sort.class))).thenReturn(List.of(existente()));
        assertEquals(1, servicio.listar(null).size());
    }

    @Test
    void listar_por_estudiante_filtra_por_quien_reporto() {
        when(repositorio.findByReportadoPor_IdOrderById(2)).thenReturn(List.of(existente()));
        assertEquals(1, servicio.listar(2).size());
        verify(repositorio, never()).findAll(any(Sort.class));
    }
}
