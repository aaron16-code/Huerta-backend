package cl.duoc.huertoescolar.huertoapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.huertoescolar.huertoapi.dto.UsuarioDto;
import cl.duoc.huertoescolar.huertoapi.model.Rol;
import cl.duoc.huertoescolar.huertoapi.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import cl.duoc.huertoescolar.huertoapi.model.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository repositorio;
    @InjectMocks private UsuarioService servicio;

    @Test
    void crear_estudiante_sin_grupo_da_error_400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.crear(new UsuarioDto(null, "Sofía", Rol.ESTUDIANTE, " ")));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(repositorio, never()).save(any());
    }

    @Test
    void crear_docente_sin_grupo_se_guarda() {
        when(repositorio.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDto guardado = servicio.crear(new UsuarioDto(null, "  Laura ", Rol.ADMINISTRADOR, null));

        assertEquals("Laura", guardado.nombre());
        assertEquals("", guardado.grupo());
        assertEquals(Rol.ADMINISTRADOR, guardado.rol());
    }

    @Test
    void obtener_un_usuario_que_no_existe_da_404() {
        when(repositorio.findById(7)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.obtener(7));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void eliminar_un_usuario_que_no_existe_da_404_y_no_borra() {
        when(repositorio.findById(7)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> servicio.eliminar(7));
        verify(repositorio, never()).deleteById(any());
    }
}
