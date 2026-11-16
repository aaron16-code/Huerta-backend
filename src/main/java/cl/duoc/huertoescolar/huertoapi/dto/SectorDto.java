package cl.duoc.huertoescolar.huertoapi.dto;

import cl.duoc.huertoescolar.huertoapi.model.EstadoSector;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SectorDto(
        Integer id,
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        String descripcion,
        String ubicacion,
        @NotNull(message = "El estado es obligatorio") EstadoSector estado,
        Integer responsableId
) {
}
