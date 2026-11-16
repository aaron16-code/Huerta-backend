package cl.duoc.huertoescolar.huertoapi.dto;

import cl.duoc.huertoescolar.huertoapi.model.EstadoTarea;
import cl.duoc.huertoescolar.huertoapi.model.TipoTarea;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Tarea "plana": las relaciones viajan como ids (sectorId, creadaPorId...), con los mismos nombres
 * que usa la data class Tarea de la app. La entidad con relaciones queda dentro del servidor.
 */
public record TareaDto(
        Integer id,
        @NotNull(message = "El tipo es obligatorio") TipoTarea tipo,
        @NotBlank(message = "La descripción es obligatoria") @Size(max = 255) String descripcion,
        @NotBlank(message = "Las instrucciones son obligatorias") @Size(max = 1000) String instrucciones,
        @NotNull(message = "El sector es obligatorio") Integer sectorId,
        Integer cultivoId,
        Long fechaCreacion,
        @NotNull(message = "La fecha programada es obligatoria") Long fechaProgramada,
        Integer responsableUsuarioId,
        String grupoAsignado,
        @NotNull(message = "Falta quién creó la tarea") Integer creadaPorId,
        EstadoTarea estado,
        Long fechaRealizacion,
        Integer realizadaPorId,
        String fotoUri,
        String comentarioEstudiante,
        String comentarioDocente,
        Integer validadorId,
        Long fechaValidacion
) {
}
