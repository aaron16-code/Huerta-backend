package cl.duoc.huertoescolar.huertoapi.dto;

import cl.duoc.huertoescolar.huertoapi.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Lo que entra y sale por la API para un usuario. */
public record UsuarioDto(
        Integer id,
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotNull(message = "El rol es obligatorio") Rol rol,
        String grupo
) {
}
