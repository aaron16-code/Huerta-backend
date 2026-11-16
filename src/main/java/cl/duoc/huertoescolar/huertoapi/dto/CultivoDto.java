package cl.duoc.huertoescolar.huertoapi.dto;

import cl.duoc.huertoescolar.huertoapi.model.EstadoCultivo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CultivoDto(
        Integer id,
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        String descripcion,
        @NotNull(message = "El sector es obligatorio") Integer sectorId,
        String fechaSiembra,
        String fechaCosechaEstimada,
        @NotNull(message = "La frecuencia de riego es obligatoria")
        @Min(value = 1, message = "La frecuencia de riego debe ser al menos 1 día") Integer frecuenciaRiegoDias,
        EstadoCultivo estado,
        String observaciones,
        String fotoUri
) {
}
